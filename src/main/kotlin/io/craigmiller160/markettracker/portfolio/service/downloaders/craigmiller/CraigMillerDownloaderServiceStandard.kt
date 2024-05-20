package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MAX
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MIN
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService.Companion.RELEVANT_ACTIONS
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService.Companion.SYMBOL_EXCLUSIONS
import java.math.BigDecimal
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec

@Service
class CraigMillerDownloaderServiceStandard(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val webClient: WebClient
) : ChildDownloaderService {
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun download(token: String): TryEither<List<PortfolioWithHistory>> =
      downloaderConfig.portfolioSpreadsheetsStandard
          .map { config -> config.name to getTransactionDataFromSpreadsheet(config, token) }
          .map { (name, response) ->
            response.awaitBodyResult<GoogleSpreadsheetValues>().map { name to it }
          }
          .bindToList()
          .flatMap { responsesToPortfolios(it) }

  private fun responsesToPortfolios(
      responses: List<Pair<String, GoogleSpreadsheetValues>>
  ): TryEither<List<PortfolioWithHistory>> {
    log.debug("Parsing and formatting google spreadsheet responses")
    return responses.map { (name, response) -> transformResponse(name, response) }.bindToList()
  }

  private fun transformResponse(
      portfolioName: String,
      response: GoogleSpreadsheetValues
  ): TryEither<PortfolioWithHistory> {
    val portfolioId = TypedId<PortfolioId>()
    return response.values
        .drop(1)
        .map { CraigMillerTransactionRecord.fromRaw(it) }
        .bindToList()
        .map { recordsToSharesOwned(portfolioId, it) }
        .map { ownershipHistory ->
          PortfolioWithHistory(
              id = portfolioId,
              name = portfolioName,
              userId = downloaderConfig.userId,
              ownershipHistory = ownershipHistory)
        }
  }

  private fun recordsToSharesOwned(
      portfolioId: TypedId<PortfolioId>,
      records: List<CraigMillerTransactionRecord>
  ): List<SharesOwned> =
      records
          .asSequence()
          .filter { RELEVANT_ACTIONS.contains(it.action) }
          .filter { record -> !SYMBOL_EXCLUSIONS.any { regex -> regex.matches(record.symbol) } }
          .sortedWith(CraigMillerTransactionRecord.comparator)
          .map(initialRecord(portfolioId))
          .fold(
              OwnershipContext(sharesOwnedMap = persistentMapOf()),
              ownershipContextCombine(downloaderConfig.userId, portfolioId))
          .sharesOwnedMap
          .values
          .flatten()

  private fun initialRecord(
      portfolioId: TypedId<PortfolioId>
  ): (CraigMillerTransactionRecord) -> OwnershipContext = { record ->
    val sharesOwned =
        SharesOwned(
            id = TypedId(),
            userId = downloaderConfig.userId,
            portfolioId = portfolioId,
            dateRangeStart = record.date,
            dateRangeEnd = DATE_RANGE_MAX,
            symbol = record.symbol,
            totalShares = record.shares)
    OwnershipContext(
        sharesOwnedMap = persistentMapOf(record.symbol to persistentListOf(sharesOwned)),
        record = record)
  }

  private fun getTransactionDataFromSpreadsheet(
      config: PortfolioConfig,
      accessToken: String
  ): ResponseSpec {
    log.debug(
        "Downloading data from spreadsheet. Sheet: ${config.sheetId} Values: ${config.valuesRange}")
    return webClient
        .get()
        .uri(
            "${downloaderConfig.googleSheetsApiBaseUrl}/spreadsheets/${config.sheetId}/values/${config.valuesRange}")
        .header("Authorization", "Bearer $accessToken")
        .retrieveSuccess()
  }
}

private data class OwnershipContext(
    val sharesOwnedMap: PersistentMap<String, PersistentList<SharesOwned>>,
    val record: CraigMillerTransactionRecord? = null
)

private fun ownershipContextCombine(
    userId: TypedId<UserId>,
    portfolioId: TypedId<PortfolioId>
): (OwnershipContext, OwnershipContext) -> OwnershipContext = { ctx1, ctx2 ->
  val sharesOwnedList = ctx1.sharesOwnedMap[ctx2.record?.symbol] ?: persistentListOf()
  val lastSharesOwned = sharesOwnedList.lastOrNull()
  val lastTotalShares = lastSharesOwned?.totalShares ?: BigDecimal("0")
  val replaceLastSharesOwned =
      lastSharesOwned?.dateRangeStart == ctx2.record?.date ?: DATE_RANGE_MIN

  val totalShares =
      when (ctx2.record?.action) {
        Action.BUY,
        Action.BONUS -> lastTotalShares + ctx2.record.shares
        Action.SELL -> lastTotalShares - ctx2.record.shares
        else -> BigDecimal("0")
      }

  val newSharesOwned =
      SharesOwned(
          id = TypedId(),
          userId = userId,
          portfolioId = portfolioId,
          dateRangeStart = ctx2.record?.date ?: DATE_RANGE_MIN,
          dateRangeEnd = DATE_RANGE_MAX,
          symbol = ctx2.record?.symbol ?: "",
          totalShares = totalShares)

  val newMap =
      ctx1.sharesOwnedMap.mutate { map ->
        map[ctx2.record?.symbol ?: ""] =
            sharesOwnedList.mutate { list ->
              if (replaceLastSharesOwned) {
                list[list.size - 1] = newSharesOwned
              } else {
                lastSharesOwned?.let { lastSharesOwnedReal ->
                  list[list.size - 1] =
                      lastSharesOwnedReal.copy(dateRangeEnd = ctx2.record?.date ?: DATE_RANGE_MAX)
                }
                list += newSharesOwned
              }
            }
      }
  OwnershipContext(sharesOwnedMap = newMap)
}