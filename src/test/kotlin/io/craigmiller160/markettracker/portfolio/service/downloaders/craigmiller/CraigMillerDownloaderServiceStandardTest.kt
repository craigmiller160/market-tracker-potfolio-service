package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
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
        TestDispatcher(
            baseUrl = downloaderConfig.googleSheetsApiBaseUrl,
            expectedToken = googleApiAccessToken.accessToken,
            spreadsheetUrlValues = downloaderConfig.portfolioSpreadsheetsStandard,
            transactions = transactions1)
    mockServer.start(testPort)
  }

  @AfterEach
  fun cleanup() {
    mockServer.shutdown()
  }

  private fun writeDataForDebugging(
      index: Int,
      expected: List<SharesOwned>,
      actual: List<SharesOwned>
  ) {
    val outputPath = Paths.get(System.getProperty("user.dir"), "build", "craigmiller_download")
    Files.createDirectories(outputPath)
    Files.write(
        outputPath.resolve("expected$index.json"),
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(expected))
    Files.write(
        outputPath.resolve("actual$index.json"),
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(actual))
  }

  @Test
  fun `downloads and formats google sheet data`() {

    val result = runBlocking { service.download(googleApiAccessToken.accessToken) }.shouldBeRight()

    result.shouldHaveSize(3)
    mockServer.requestCount.shouldBe(3)

    result.forEachIndexed { index, portfolio ->
      portfolio.name.shouldBeIn("Brokerage", "Roth IRA", "Rollover IRA")
      val expectedSharesOwned =
          TEST_DATA.map { it.copy(portfolioId = portfolio.id, userId = downloaderConfig.userId) }
              .sortedWith(::sort)
      val actualSharesOwned = portfolio.ownershipHistory.sortedWith(::sort)

      writeDataForDebugging(index, expectedSharesOwned, actualSharesOwned)

      actualSharesOwned.shouldHaveSize(expectedSharesOwned.size).forEachIndexed { innerIndex, actual
        ->
        Either.catch {
              val expected = expectedSharesOwned[innerIndex]
              actual.userId.shouldBe(expected.userId)
              actual.portfolioId.shouldBe(expected.portfolioId)
              actual.dateRangeStart.shouldBe(expected.dateRangeStart)
              actual.dateRangeEnd.shouldBe(expected.dateRangeEnd)
              actual.symbol.shouldBe(expected.symbol)
              actual.totalShares.shouldBeEqualComparingTo(expected.totalShares)
            }
            .shouldBeRight { ex -> "Error validating record $innerIndex: ${ex.message}" }
      }
    }
  }
}

private class TestDispatcher(
    private val baseUrl: String,
    private val spreadsheetUrlValues: List<PortfolioConfig>,
    private val expectedToken: String,
    private val transactions: String
) : Dispatcher() {
  override fun dispatch(request: RecordedRequest): MockResponse {
    val authHeader = request.headers["Authorization"] ?: return MockResponse().setResponseCode(401)
    if (authHeader != "Bearer $expectedToken") {
      println("Missing expected authorization header")
      return MockResponse().setResponseCode(401).setBody("Missing expected authorization header")
    }

    val url = request.requestUrl?.toString() ?: ""
    val expectedUrlRegex =
        Regex("^https://${baseUrl}/spreadsheets/(?<sheedId>.+)/values/(?<valuesRange>.+)\$")
    val matchResult = expectedUrlRegex.find(url)

    if (matchResult == null) {
      println("Request URL does not match regex: $url")
      return MockResponse().setResponseCode(404).setBody("Request URL does not match regex: $url")
    }

    val sheetId = matchResult.groups["sheetId"] ?: ""
    val valuesRange = matchResult.groups["valuesRange"] ?: ""
    val matchingUrlValues =
        spreadsheetUrlValues.find { values ->
          values.sheetId == sheetId && values.valuesRange == valuesRange
        }

    if (matchingUrlValues == null) {
      println(
          "Request URL does not have required path elements: SheetId=$sheetId ValuesRange=$valuesRange")
      return MockResponse()
          .setResponseCode(404)
          .setBody(
              "Request URL does not have required path elements: SheetId=$sheetId ValuesRange=$valuesRange")
    }

    return MockResponse().setResponseCode(200).setBody(transactions)
  }
}

private fun sort(one: SharesOwned, two: SharesOwned): Int {
  val symbolCompare = one.symbol.compareTo(two.symbol)
  if (symbolCompare != 0) {
    return symbolCompare
  }
  return one.dateRangeStart.compareTo(two.dateRangeStart)
}
