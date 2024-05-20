package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
      downloaderConfig.portfolioSpreadsheets401k
          .map { config -> downloadSpreadsheetAsync(config, token) }
          .awaitAll()
          .bindToList()
          .flatMap { responsesToPortfolios(it) }
    }
  }

  private fun responsesToPortfolios(
      responses: List<Pair<PortfolioConfig, GoogleSpreadsheetValues>>
  ): TryEither<List<PortfolioWithHistory>> {
    TODO()
  }
}
