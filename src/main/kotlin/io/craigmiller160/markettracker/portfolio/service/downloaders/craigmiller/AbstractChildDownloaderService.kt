package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient

typealias DownloadSpreadsheetResult = TryEither<Pair<PortfolioConfig, GoogleSpreadsheetValues>>

abstract class AbstractChildDownloaderService(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val webClient: WebClient
) : ChildDownloaderService {
  private val log = LoggerFactory.getLogger(javaClass)

  protected suspend fun downloadSpreadsheet(
      config: PortfolioConfig,
      accessToken: String
  ): DownloadSpreadsheetResult {
    log.debug(
        "Downloading data from spreadsheet. Sheet: ${config.sheetId} Values: ${config.valuesRange}")
    return webClient
        .get()
        .uri(
            "${downloaderConfig.googleSheetsApiBaseUrl}/spreadsheets/${config.sheetId}/values/${config.valuesRange}")
        .header("Authorization", "Bearer $accessToken")
        .retrieveSuccess()
        .awaitBodyResult<GoogleSpreadsheetValues>()
        .map { config to it }
  }

  protected suspend fun downloadSpreadsheetAsync(
      config: PortfolioConfig,
      token: String
  ): Deferred<DownloadSpreadsheetResult> = coroutineScope {
    async { downloadSpreadsheet(config, token) }
  }
}
