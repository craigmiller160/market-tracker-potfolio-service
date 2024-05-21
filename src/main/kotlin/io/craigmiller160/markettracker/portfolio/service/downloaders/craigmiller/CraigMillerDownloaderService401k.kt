package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig401k
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
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
    response.values.drop(1).map { cols -> cols[0].toDate() to cols[7].toAmount() }
    TODO()
  }
}

private val SPREADSHEET_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")

private fun String.toDate(): LocalDate = YearMonth.parse(this, SPREADSHEET_DATE_FORMAT).atDay(1)

private fun String.toAmount(): BigDecimal = this.replace(Regex("^\\$"), "").let { BigDecimal(it) }
