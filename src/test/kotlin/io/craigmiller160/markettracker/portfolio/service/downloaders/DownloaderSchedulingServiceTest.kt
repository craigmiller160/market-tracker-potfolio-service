package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import org.junit.jupiter.api.Test

class DownloaderSchedulingServiceTest {
  private val transactions1: String = DataLoader.load("data/craigmiller/Transactions1.json")
  private val transactions2: String = DataLoader.load("data/craigmiller/Transactions2.json")
  @Test
  fun `downloads data and writes to database`() {
    TODO()
  }
}
