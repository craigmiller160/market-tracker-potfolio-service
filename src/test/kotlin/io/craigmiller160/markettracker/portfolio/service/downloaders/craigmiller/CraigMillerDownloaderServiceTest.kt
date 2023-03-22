package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import java.math.BigDecimal
import java.time.LocalDate
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
    private val expectedData: List<SharesOwned> =
        listOf(
            createSharesOwned(
                LocalDate.of(2020, 6, 4), LocalDate.of(2020, 6, 18), "COTY", BigDecimal("1")),
            createSharesOwned(
                LocalDate.of(2020, 6, 19),
                CraigMillerDownloaderService.MAX_DATE,
                "COTY",
                BigDecimal("0")),
            createSharesOwned(
                LocalDate.of(2020, 6, 15),
                CraigMillerDownloaderService.MAX_DATE,
                "UAL",
                BigDecimal("0")),
            createSharesOwned(
                LocalDate.of(2020, 6, 10), LocalDate.of(2020, 6, 14), "VYM", BigDecimal("1")),
            createSharesOwned(
                LocalDate.of(2020, 6, 15), LocalDate.of(2020, 6, 21), "VYM", BigDecimal("2")),
            createSharesOwned(
                LocalDate.of(2020, 6, 22), LocalDate.of(2020, 10, 17), "VYM", BigDecimal("4")),
            createSharesOwned(
                LocalDate.of(2020, 10, 18),
                CraigMillerDownloaderService.MAX_DATE,
                "VYM",
                BigDecimal("6")))

    private fun createSharesOwned(
        start: LocalDate,
        end: LocalDate,
        symbol: String,
        totalShares: BigDecimal
    ): SharesOwned =
        SharesOwned(
            id = TypedId(),
            portfolioId = TypedId(),
            userId = TypedId(),
            dateRangeStart = start,
            dateRangeEnd = end,
            totalShares = totalShares,
            symbol = symbol)
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
