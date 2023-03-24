package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["downloaders.enable"], havingValue = "")
class DownloaderSchedulingService(
    private val craigMillerDownloaderService: CraigMillerDownloaderService
) {
  @Scheduled(fixedRateString = "#{\${downloaders.interval-rate-hours} * 60 * 60 * 1000}")
  fun downloadAtInterval() {
    GlobalScope.launch { craigMillerDownloaderService.download() }
  }
}
