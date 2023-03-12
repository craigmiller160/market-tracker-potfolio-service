package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DownloaderSchedulingService(
    private val craigMillerDownloaderService: CraigMillerDownloaderService
) {
  @Scheduled(cron = "\${downloaders.interval-cron}")
  fun executeDownloads() {
    GlobalScope.launch { craigMillerDownloaderService.download() }
  }

  // TODO delete this
  @PostConstruct
  fun forTestingOnly() {
    GlobalScope.launch { craigMillerDownloaderService.download() }
  }
}
