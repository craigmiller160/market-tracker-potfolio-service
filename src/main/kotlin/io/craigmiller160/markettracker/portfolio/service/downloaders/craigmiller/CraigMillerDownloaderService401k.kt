package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.config.Allocation401k
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig401k
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.isBetween
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierDay
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierHistory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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
  companion object {
    const val US_SYMBOL = "VTI"
    const val EX_US_SYMBOL = "VXUS"
  }

  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun download(token: String): ChildDownloadServiceResult = coroutineScope {
    either {
      val tradierHistory = tradierService.getTradierHistory(listOf(US_SYMBOL, EX_US_SYMBOL)).bind()
      val spreadsheets =
          downloaderConfig.portfolioSpreadsheets401k
              .map { config -> async { downloadSpreadsheet(config, token) } }
              .awaitAll()
              .bindToList()
              .bind()

      log.debug("Parsing and formatting google spreadsheet responses")
      spreadsheets.map { (config, response) ->
        responseToPortfolio(config as PortfolioConfig401k, response, tradierHistory)
      }
    }
  }

  private fun responseToPortfolio(
      config: PortfolioConfig401k,
      response: GoogleSpreadsheetValues,
      tradierHistory: Map<String, TradierHistory>
  ): PortfolioWithHistory {
    val doConvertToSharesOwned = convertToSharesOwned(config, tradierHistory)
    response.values
        .drop(1)
        .map { cols -> cols[0].toDate() to cols[7].toAmount() }
        .map { (date, amount) -> doConvertToSharesOwned(date, amount) }
        .bindToList()
        .map { list -> list.flatten() }
    TODO()
  }

  private fun convertToSharesOwned(
      config: PortfolioConfig401k,
      tradierHistory: Map<String, TradierHistory>
  ): (LocalDate, BigDecimal) -> TryEither<List<SharesOwned>> = { date, amount ->
    getConversionValues(config, tradierHistory, date).map { values ->
      val totalAmountUs = amount.times(values.allocation.percentUs.toBigDecimal())
      val totalAmountExUs = amount.times(values.allocation.percentExUs.toBigDecimal())
      val usShares = totalAmountUs.divide(values.usHistory.close.toBigDecimal())
      val exUsShares = totalAmountExUs.divide(values.exUsHistory.close.toBigDecimal())

      val usSharesOwned =
          SharesOwned(
              id = TypedId(),
              userId = TODO(),
              portfolioId = TODO(),
              dateRangeStart = TODO(),
              dateRangeEnd = TODO(),
              symbol = US_SYMBOL,
              totalShares = usShares)
      val exUsSharesOwned =
          SharesOwned(
              id = TypedId(),
              userId = TODO(),
              portfolioId = TODO(),
              dateRangeStart = TODO(),
              dateRangeEnd = TODO(),
              symbol = EX_US_SYMBOL,
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
        tradierHistory[US_SYMBOL]
            ?.history
            ?.day
            ?.find { it.date.monthValue == date.monthValue }
            .leftIfNull("Unable to find matching US history for date $date")
    val exUsHistoryEither =
        tradierHistory[EX_US_SYMBOL]
            ?.history
            ?.day
            ?.find { it.date.monthValue == date.monthValue }
            .leftIfNull("Unable to find matching EX-US history for date $date")

    return either {
      val allocation = matchingAllocationEither.bind()
      val usHistory = usHistoryEither.bind()
      val exUsHistory = exUsHistoryEither.bind()

      ConversionValues(allocation = allocation, usHistory = usHistory, exUsHistory = exUsHistory)
    }
  }
}

private data class ConversionValues(
    val allocation: Allocation401k,
    val usHistory: TradierDay,
    val exUsHistory: TradierDay
)

private val SPREADSHEET_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")

private fun String.toDate(): LocalDate = YearMonth.parse(this, SPREADSHEET_DATE_FORMAT).atDay(1)

private fun String.toAmount(): BigDecimal = this.replace(Regex("^\\$"), "").let { BigDecimal(it) }
