package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["downloaders.enable"], havingValue = "")
class DownloaderSchedulingService(
    private val craigMillerDownloaderService: CraigMillerDownloaderService,
    private val persistDownloadService: PersistDownloadService
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(fixedRateString = "#{\${downloaders.interval-rate-hours} * 60 * 60 * 1000}")
  suspend fun downloadAtInterval(): KtResult<Unit> =
      craigMillerDownloaderService
          .download()
          .flatMap { persistDownloadService.persistPortfolios(it) }
          .onFailure { ex -> log.error("Error downloading portfolio data", ex) }
          .map { Unit }
}
