package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DownloaderOrchestrationService(
    private val craigMillerDownloaderService: CraigMillerDownloaderService,
    private val persistDownloadService: PersistDownloadService
) {
  private val log = LoggerFactory.getLogger(javaClass)

  suspend fun download(): TryEither<Unit> =
      craigMillerDownloaderService
          .download()
          .flatMap { persistDownloadService.persistPortfolios(it) }
          .mapLeft { ex -> ex.also { log.error("Error downloading portfolio data", it) } }
          .map { Unit }
}
