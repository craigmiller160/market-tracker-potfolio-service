package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient

abstract class AbstractChildDownloaderService(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val webClient: WebClient
) : ChildDownloaderService {
  private val log = LoggerFactory.getLogger(javaClass)

  protected suspend fun downloadSpreadsheet(
      config: PortfolioConfig,
      accessToken: String
  ): TryEither<Pair<String, GoogleSpreadsheetValues>> {
    log.debug(
        "Downloading data from spreadsheet. Sheet: ${config.sheetId} Values: ${config.valuesRange}")
    return webClient
        .get()
        .uri(
            "${downloaderConfig.googleSheetsApiBaseUrl}/spreadsheets/${config.sheetId}/values/${config.valuesRange}")
        .header("Authorization", "Bearer $accessToken")
        .retrieveSuccess()
        .awaitBodyResult<GoogleSpreadsheetValues>()
        .map { config.name to it }
  }
}
