package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["downloaders.enable-schedule"], havingValue = "true")
class DownloaderSchedulingService(
    private val downloaderOrchestrationService: DownloaderOrchestrationService
) {

  @Scheduled(fixedRateString = "#{\${downloaders.interval-rate-hours} * 60 * 60 * 1000}")
  fun downloadAtInterval(): TryEither<Unit> = runBlocking {
    downloaderOrchestrationService.download()
  }
}
