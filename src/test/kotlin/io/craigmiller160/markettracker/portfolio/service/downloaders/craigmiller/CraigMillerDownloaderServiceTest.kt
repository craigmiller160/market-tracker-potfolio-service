package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value

@MarketTrackerPortfolioIntegrationTest
class CraigMillerDownloaderServiceTest(
    @Value("\${downloaders.craigmiller.test-port}") private val testPort: Int,
    private val service: CraigMillerDownloaderService,
    private val objectMapper: ObjectMapper,
    private val downloaderConfig: CraigMillerDownloaderConfig,
) {
  companion object {
    private val transactions1: String = DataLoader.load("data/craigmiller/Transactions1.json")
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
    repeat(3) {
      mockServer.enqueue(
          MockResponse().apply {
            status = "200"
            setBody(transactions1)
          })
    }

    val values = objectMapper.readValue(transactions1, GoogleSpreadsheetValues::class.java)
    TODO()
  }
}
