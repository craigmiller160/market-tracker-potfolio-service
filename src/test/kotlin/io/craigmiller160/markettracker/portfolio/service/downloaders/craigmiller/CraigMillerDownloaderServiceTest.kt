package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value

@MarketTrackerPortfolioIntegrationTest
class CraigMillerDownloaderServiceTest(
    @Value("\${downloaders.craigmiller.test-port}") private val testPort: Int
) {
  companion object {
    private val transactions1: String = DataLoader.load("data/craigmiller/Transactions1.json")
    private val transactions2: String = DataLoader.load("data/craigmiller/Transactions2.json")
  }

  private val mockServer = MockWebServer()

  @BeforeEach
  fun setup() {
    mockServer.start(testPort)
  }

  @AfterEach
  fun cleanup() {
    mockServer.shutdown()
  }

  @Test
  fun `downloads and formats google sheet data`() {
    TODO()
  }
}
