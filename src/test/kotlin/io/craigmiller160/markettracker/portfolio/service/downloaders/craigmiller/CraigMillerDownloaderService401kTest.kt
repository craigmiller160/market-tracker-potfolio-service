package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@MarketTrackerPortfolioIntegrationTest
class CraigMillerDownloaderService401kTest
@Autowired
constructor(
    @Value("\${downloaders.craigmiller.test-port}") private val testGooglePort: Int,
    @Value("\${market-tracker-api.test-port}") private val testMarketTrackerPort: Int,
    private val service: CraigMillerDownloaderService401k,
    private val objectMapper: ObjectMapper,
    private val downloaderConfig: CraigMillerDownloaderConfig,
) {
  companion object {
    private val data401k: String = DataLoader.load("data/craigmiller/Data401k.json")
    private val googleApiAccessToken =
        GoogleApiAccessToken(accessToken = "TOKEN", expiresIn = 100000, tokenType = "Bearer")
  }

  private val mockGoogleServer = MockWebServer()

  @BeforeEach
  fun setup() {
    mockGoogleServer.dispatcher =
        GoogleSheetsDispatcher(
            baseUrl = downloaderConfig.googleSheetsApiBaseUrl,
            expectedToken = googleApiAccessToken.accessToken,
            spreadsheetUrlValues = downloaderConfig.portfolioSpreadsheetsStandard,
            response = data401k)
    mockGoogleServer.start(testGooglePort)
  }

  @AfterEach
  fun cleanup() {
    mockGoogleServer.shutdown()
  }

  @Test
  fun `downloads and formats google sheet data with special conversion for 401k`() {
    val result = runBlocking { service.download(googleApiAccessToken.accessToken) }.shouldBeRight()

    result.shouldHaveSize(1)
    mockGoogleServer.requestCount.shouldBe(1)
    TODO()
  }
}
