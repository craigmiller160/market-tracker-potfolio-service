package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.config.Allocation401k
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig401k
import io.craigmiller160.markettracker.portfolio.config.percentExUsAsFraction
import io.craigmiller160.markettracker.portfolio.config.percentUsAsFraction
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MAX
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.isBetween
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierDay
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierHistory
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService401k(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val tradierService: TradierService,
    webClient: WebClient
) : AbstractChildDownloaderService(downloaderConfig, webClient) {
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun download(token: String): ChildDownloadServiceResult = coroutineScope {
    either {
      val tradierHistory =
          tradierService
              .getTradierHistory(
                  listOf(downloaderConfig.etfEquivalents.us, downloaderConfig.etfEquivalents.exUs))
              .bind()
      val spreadsheets =
          downloaderConfig.portfolioSpreadsheets401k
              .map { config -> async { downloadSpreadsheet(config, token) } }
              .awaitAll()
              .bindAll()

      log.debug("Parsing and formatting google spreadsheet responses")
      spreadsheets
          .map { (config, response) ->
            responseToPortfolio(config as PortfolioConfig401k, response, tradierHistory)
          }
          .bindAll()
    }
  }

  private fun responseToPortfolio(
      config: PortfolioConfig401k,
      response: GoogleSpreadsheetValues,
      tradierHistory: Map<String, TradierHistory>
  ): TryEither<PortfolioWithHistory> {
    val portfolioId = TypedId<PortfolioId>()
    val doConvertToSharesOwned = convertToSharesOwned(config, tradierHistory, portfolioId)
    return response.values
        .asSequence()
        .drop(1)
        .filter { it.size >= 7 }
        .map { cols -> cols[0] to cols[7] }
        .filter { (_, amount) -> amount.trim().isNotEmpty() }
        .map { (date, amount) -> date.toDate() to amount.toAmount() }
        .map { (date, amount) -> doConvertToSharesOwned(date, amount) }
        .bindToList()
        .map { cleanupSharesOwnedList(it) }
        .map { list ->
          PortfolioWithHistory(
              id = portfolioId,
              userId = downloaderConfig.userId,
              name = config.name,
              ownershipHistory = list)
        }
  }

  private fun cleanupSharesOwnedList(list: List<List<SharesOwned>>): List<SharesOwned> {
    val flattenedList = list.flatten()
    val sharesBySymbol = flattenedList.groupBy { it.symbol }
    val maxUs =
        sharesBySymbol[downloaderConfig.etfEquivalents.us]
            ?.maxBy { it.dateRangeEnd }
            ?.let { createMaxSharesOwned(it) }
    val maxExUs =
        sharesBySymbol[downloaderConfig.etfEquivalents.exUs]
            ?.maxBy { it.dateRangeEnd }
            ?.let { createMaxSharesOwned(it) }

    return flattenedList + listOfNotNull(maxExUs, maxUs)
  }

  private fun createMaxSharesOwned(existingMaxSharesOwned: SharesOwned): SharesOwned =
      SharesOwned(
          id = TypedId(),
          userId = downloaderConfig.userId,
          portfolioId = existingMaxSharesOwned.portfolioId,
          symbol = existingMaxSharesOwned.symbol,
          totalShares = existingMaxSharesOwned.totalShares,
          dateRangeStart = existingMaxSharesOwned.dateRangeEnd,
          dateRangeEnd = DATE_RANGE_MAX)

  private fun convertToSharesOwned(
      config: PortfolioConfig401k,
      tradierHistory: Map<String, TradierHistory>,
      portfolioId: TypedId<PortfolioId>
  ): (LocalDate, BigDecimal) -> TryEither<List<SharesOwned>> = { date, amount ->
    getConversionValues(config, tradierHistory, date).map { values ->
      val totalAmountUs = amount.times(values.allocation.percentUsAsFraction)
      val totalAmountExUs = amount.times(values.allocation.percentExUsAsFraction)

      val usShares = totalAmountUs.divide(values.usHistory.close.toBigDecimal(), MATH_CONTEXT)
      val exUsShares = totalAmountExUs.divide(values.exUsHistory.close.toBigDecimal(), MATH_CONTEXT)

      log.trace(
          "401k Conversion. Date=$date Amount=$amount US-Price=${values.usHistory.close} US-Mine=$totalAmountUs/$usShares Ex-US-Price=${values.exUsHistory.close} Ex-US-Mine=$totalAmountExUs/$exUsShares")

      val startDate = date.with(TemporalAdjusters.firstDayOfMonth())
      val endDate = startDate.plusMonths(1)

      val usSharesOwned =
          SharesOwned(
              id = TypedId(),
              userId = downloaderConfig.userId,
              portfolioId = portfolioId,
              dateRangeStart = startDate,
              dateRangeEnd = endDate,
              symbol = downloaderConfig.etfEquivalents.us,
              totalShares = usShares)
      val exUsSharesOwned =
          SharesOwned(
              id = TypedId(),
              userId = downloaderConfig.userId,
              portfolioId = portfolioId,
              dateRangeStart = startDate,
              dateRangeEnd = endDate,
              symbol = downloaderConfig.etfEquivalents.exUs,
              totalShares = exUsShares)

      listOf(usSharesOwned, exUsSharesOwned)
    }
  }

  private fun getConversionValues(
      config: PortfolioConfig401k,
      tradierHistory: Map<String, TradierHistory>,
      date: LocalDate
  ): TryEither<ConversionValues> {
    val matchingAllocationEither =
        config.allocations
            .find { allocation ->
              date.isBetween(allocation.startDate, allocation.endDate ?: LocalDate.MAX)
            }
            .leftIfNull("Unable to find matching allocation for date $date")

    val usHistoryEither =
        tradierHistory[downloaderConfig.etfEquivalents.us]
            ?.history
            ?.day
            ?.find { it.date.monthValue == date.monthValue && it.date.year == date.year }
            .leftIfNull("Unable to find matching US history for date $date")
    val exUsHistoryEither =
        tradierHistory[downloaderConfig.etfEquivalents.exUs]
            ?.history
            ?.day
            ?.find { it.date.monthValue == date.monthValue && it.date.year == date.year }
            .leftIfNull("Unable to find matching EX-US history for date $date")

    return either {
      val allocation = matchingAllocationEither.bind()
      val usHistory = usHistoryEither.bind()
      val exUsHistory = exUsHistoryEither.bind()

      ConversionValues(allocation = allocation, usHistory = usHistory, exUsHistory = exUsHistory)
    }
  }
}

private val MATH_CONTEXT = MathContext(4, RoundingMode.HALF_UP)

private data class ConversionValues(
    val allocation: Allocation401k,
    val usHistory: TradierDay,
    val exUsHistory: TradierDay
)

private val SPREADSHEET_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")

private fun String.toDate(): LocalDate = YearMonth.parse(this, SPREADSHEET_DATE_FORMAT).atDay(1)

private fun String.toAmount(): BigDecimal =
    this.replace(Regex("^\\$"), "").replace(Regex(","), "").let { BigDecimal(it) }
