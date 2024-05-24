package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldBeIn
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
class CraigMillerDownloaderServiceStandardTest
@Autowired
constructor(
    @Value("\${downloaders.craigmiller.test-port}") private val testPort: Int,
    private val service: CraigMillerDownloaderServiceStandard,
    private val objectMapper: ObjectMapper,
    private val downloaderConfig: CraigMillerDownloaderConfig,
) {
  companion object {
    private val transactions1: String = DataLoader.load("data/craigmiller/Transactions1.json")
    private val googleApiAccessToken =
        GoogleApiAccessToken(accessToken = "TOKEN", expiresIn = 100000, tokenType = "Bearer")
  }

  private val mockServer = MockWebServer()

  @BeforeEach
  fun setup() {
    mockServer.dispatcher =
        GoogleSheetsDispatcher(
            baseUrl = downloaderConfig.googleSheetsApiBaseUrl,
            expectedToken = googleApiAccessToken.accessToken,
            spreadsheetUrlValues = downloaderConfig.portfolioSpreadsheetsStandard,
            response = transactions1)
    mockServer.start(testPort)
  }

  @AfterEach
  fun cleanup() {
    mockServer.shutdown()
  }

  @Test
  fun `downloads and formats standard transactional google sheet data`() {
    val result = runBlocking { service.download(googleApiAccessToken.accessToken) }.shouldBeRight()

    result.shouldHaveSize(3)
    mockServer.requestCount.shouldBe(3)

    result.forEachIndexed { index, portfolio ->
      portfolio.name.shouldBeIn("Brokerage", "Roth IRA", "Rollover IRA")
      val expectedSharesOwned =
          TEST_DATA_STANDARD.map {
                it.copy(portfolioId = portfolio.id, userId = downloaderConfig.userId)
              }
              .sortedWith(::sort)
      val actualSharesOwned = portfolio.ownershipHistory.sortedWith(::sort)

      writeDataForDebugging(objectMapper, "standard", index, expectedSharesOwned, actualSharesOwned)

      validateSharesOwned(expectedSharesOwned, actualSharesOwned)
    }
  }
}

private fun sort(one: SharesOwned, two: SharesOwned): Int {
  val symbolCompare = one.symbol.compareTo(two.symbol)
  if (symbolCompare != 0) {
    return symbolCompare
  }
  return one.dateRangeStart.compareTo(two.dateRangeStart)
}
