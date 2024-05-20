package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService401k(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    webClient: WebClient
) : AbstractChildDownloaderService(downloaderConfig, webClient) {
  override suspend fun download(token: String): ChildDownloadServiceResult = coroutineScope {
    async {
      downloaderConfig.portfolioSpreadsheets401k.map { config ->
        downloadSpreadsheetAsync(config, token)
        // TODO need to integrate full config for each one
      }
      Either.Right(listOf())
    }
  }
}
