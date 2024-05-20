package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.MarketTrackerApiConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
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
    private val webClient: WebClient,
    private val marketTrackerApiConfig: MarketTrackerApiConfig
) : AbstractChildDownloaderService(downloaderConfig, webClient) {

  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun download(token: String): ChildDownloadServiceResult = coroutineScope {
    async {
      // TODO download the tradier data
      downloaderConfig.portfolioSpreadsheets401k
          .map { config -> downloadSpreadsheetAsync(config, token) }
          .awaitAll()
          .bindToList()
          .flatMap { responsesToPortfolios(it) }
    }
  }

  private suspend fun downloadTradierData(symbol: String) {
    val today = LocalDate.now().format(TRADIER_DATE_FORMAT)
    webClient
        .get()
        .uri(
            "${marketTrackerApiConfig.host}/tradier/markets/history?symbol=${symbol}&start=2015-01-01&end=$today&interval=monthly")
        .header("Authorization", "Bearer XYZ")
        .retrieveSuccess()
        .awaitBodyResult<Any>() // TODO fix this
  }

  private fun responsesToPortfolios(
      responses: List<Pair<PortfolioConfig, GoogleSpreadsheetValues>>
  ): TryEither<List<PortfolioWithHistory>> {
    log.debug("Parsing and formatting google spreadsheet responses")
    println(responses.first().second)
    TODO()
  }

  private fun transformResponse(
      portfolioName: String,
      response: GoogleSpreadsheetValues
  ): TryEither<Any> {
    response.values.drop(1).map { cols -> cols[0].toDate() to cols[7].toAmount() }
    TODO()
  }
}

private val TRADIER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val SPREADSHEET_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")

private fun String.toDate(): LocalDate = YearMonth.parse(this, SPREADSHEET_DATE_FORMAT).atDay(1)

private fun String.toAmount(): BigDecimal = this.replace(Regex("^\\$"), "").let { BigDecimal(it) }
