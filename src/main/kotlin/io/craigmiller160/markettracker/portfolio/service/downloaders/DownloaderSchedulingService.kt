package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["downloaders.enable-schedule"], havingValue = "true")
class DownloaderSchedulingService(
    private val craigMillerDownloaderService: CraigMillerDownloaderService,
    private val persistDownloadService: PersistDownloadService
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(fixedRateString = "#{\${downloaders.interval-rate-hours} * 60 * 60 * 1000}")
  fun downloadAtInterval(): TryEither<Unit> = runBlocking {
    craigMillerDownloaderService
        .download()
        .flatMap { persistDownloadService.persistPortfolios(it) }
        .mapLeft { ex -> ex.also { log.error("Error downloading portfolio data", it) } }
        .map { Unit }
  }
}
