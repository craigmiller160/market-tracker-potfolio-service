package io.craigmiller160.markettracker.portfolio.web.routes

import arrow.core.flatMap
import arrow.core.getOrElse
import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.service.PortfolioConstants
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import io.craigmiller160.markettracker.portfolio.testutils.userTypedId
import io.craigmiller160.markettracker.portfolio.web.types.ErrorResponse
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import io.kotest.assertions.arrow.core.shouldBeRight
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.stream.Stream
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class PortfolioRoutesTest
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    private val portfolioRepo: PortfolioRepository,
    private val sharesOwnedRepo: SharesOwnedRepository,
    private val defaultUsers: DefaultUsers,
    private val objectMapper: ObjectMapper
) {
  companion object {
    @JvmStatic
    fun sharesOwnedForStock(): Stream<CoreSharesOwnedRouteParams> =
        Stream.of(
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusDays(1), SharesOwnedInterval.SINGLE),
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusDays(7), SharesOwnedInterval.DAILY),
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusMonths(1), SharesOwnedInterval.DAILY),
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusMonths(3), SharesOwnedInterval.DAILY),
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusYears(1), SharesOwnedInterval.WEEKLY),
            CoreSharesOwnedRouteParams(
                "VTI", BASE_DATE, BASE_DATE.plusYears(5), SharesOwnedInterval.MONTHLY))

    @JvmStatic
    fun sharesOwnedBadRequestParams(): Stream<Any> =
        Stream.of(
            SharesOwnedBadRequestParams(
                "2022-01-01", "2022-01-02", null, "Missing required parameter: interval"),
            SharesOwnedBadRequestParams(
                "2022-01-01", null, "DAILY", "Missing required parameter: endDate"),
            SharesOwnedBadRequestParams(
                null, "2022-01-02", "DAILY", "Missing required parameter: startDate"),
            SharesOwnedBadRequestParams(
                "2022-01-01", "2022-01-02", "abc", "Error parsing interval"),
            SharesOwnedBadRequestParams("2022-01-01", "abc", "DAILY", "Error parsing endDate"),
            SharesOwnedBadRequestParams("abc", "2022-01-02", "DAILY", "Error parsing startDate"))
  }

  private fun createData(offsetDays: Int, numRecords: Int): PortfolioRouteData = runBlocking {
    createPortfolioRouteData(defaultUsers, offsetDays, numRecords).let { data ->
      portfolioRepo
          .createAllPortfolios(data.portfolios)
          .flatMap { sharesOwnedRepo.createAllSharesOwned(data.sharesOwned) }
          .map { data }
          .getOrElse { throw it }
    }
  }

  private fun getPortfolioResponse(data: PortfolioRouteData): List<PortfolioResponse> {
    val portfolios = data.portfolios.drop(1)
    val sharesOwned = data.sharesOwned.filter { so -> portfolios.any { it.id == so.portfolioId } }
    val baseExpectedResponse =
        portfolios.map { portfolio ->
          val stocks =
              sharesOwned.filter { it.portfolioId == portfolio.id }.map { it.symbol }.distinct()
          portfolio.toPortfolioResponse(stocks)
        }
    val combinedStocks = sharesOwned.map { it.symbol }.distinct()
    val combinedPortfolio =
        PortfolioResponse(
            id = PortfolioConstants.COMBINED_PORTFOLIO_ID,
            name = PortfolioConstants.COMBINED_PORTFOLIO_NAME,
            stockSymbols = combinedStocks)
    return baseExpectedResponse + combinedPortfolio
  }

  @Test
  fun `gets list of portfolios for user`() {
    val data = createData(10, 100)
    val expectedResponse = getPortfolioResponse(data)

    doGetListOfPortfolios(expectedResponse)
  }

  private fun doGetListOfPortfolios(
      expectedResponse: List<PortfolioResponse>,
      queryString: String = ""
  ) {
    webTestClient
        .get()
        .uri("/portfolios?${queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  @Test
  fun `gets list of portfolios for user, with stocks filtered by date range`() {
    val data = createData(10, 100)
    val expectedResponse = getPortfolioResponse(data)

    val maxDate = data.sharesOwned.maxBy { it.dateRangeStart }
    runBlocking {
      sharesOwnedRepo
          .createAllSharesOwned(
              listOf(
                  SharesOwned(
                      id = TypedId(),
                      userId = defaultUsers.primaryUser.userTypedId,
                      portfolioId = expectedResponse[0].id,
                      dateRangeStart = maxDate.dateRangeStart.plusDays(10),
                      dateRangeEnd = maxDate.dateRangeStart.plusDays(100),
                      symbol = "ABC",
                      totalShares = BigDecimal("10"))))
          .shouldBeRight()
    }

    val fullExpectedResponse =
        listOf(expectedResponse[0].copy(stockSymbols = expectedResponse[0].stockSymbols + "ABC")) +
            expectedResponse.subList(1, 4) +
            listOf(
                expectedResponse[4].copy(stockSymbols = expectedResponse[4].stockSymbols + "ABC"))

    doGetListOfPortfolios(fullExpectedResponse)
    val endDate =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(maxDate.dateRangeStart.minusDays(1))
    doGetListOfPortfolios(expectedResponse, "startDate=2020-01-01&endDate=$endDate")
  }

  @Test
  fun `get shares owned history for past week for stock that user does not have in portfolio`() {
    val coreParams =
        CoreSharesOwnedRouteParams(
            "ABC", LocalDate.now(), LocalDate.now().plusDays(7), SharesOwnedInterval.DAILY)
    val numRecords = getNumRecordsForInterval(coreParams)
    val data = createData(10, numRecords)
    val params = coreParams.withKeys(defaultUsers.primaryUser.userTypedId, data.portfolios[1].id)

    webTestClient
        .get()
        .uri(
            "/portfolios/${params.portfolioId}/stocks/${params.stockSymbol}/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<SharesOwnedResponse>()))
  }

  @Test
  fun `get shares owned history for past week for stock that user does not have in all portfolios`() {
    val coreParams =
        CoreSharesOwnedRouteParams(
            "ABC", LocalDate.now(), LocalDate.now().plusDays(7), SharesOwnedInterval.DAILY)
    val numRecords = getNumRecordsForInterval(coreParams)
    val data = createData(10, numRecords)
    val params = coreParams.withKeys(defaultUsers.primaryUser.userTypedId)

    webTestClient
        .get()
        .uri(
            "/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/${params.stockSymbol}/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<SharesOwnedResponse>()))
  }

  @Test
  fun `get shares owned history for past week for stock in portfolio not owned by user`() {
    val coreParams =
        CoreSharesOwnedRouteParams(
            "VTI", LocalDate.now(), LocalDate.now().plusDays(7), SharesOwnedInterval.DAILY)
    val numRecords = getNumRecordsForInterval(coreParams)
    val data = createData(10, numRecords)
    val params = coreParams.withKeys(defaultUsers.primaryUser.userTypedId, data.portfolios[0].id)

    webTestClient
        .get()
        .uri(
            "/portfolios/${params.portfolioId}/stocks/${params.stockSymbol}/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<SharesOwnedResponse>()))
  }

  @MethodSource("sharesOwnedForStock")
  @ParameterizedTest
  fun `get shares owned for stock in portfolio`(coreParams: CoreSharesOwnedRouteParams) {
    val numRecords = getNumRecordsForInterval(coreParams)
    val data = createData(10, numRecords)
    val params = coreParams.withKeys(defaultUsers.primaryUser.userTypedId, data.portfolios[1].id)
    val expectedResponse = createSharesOwnedRouteData(data, params)

    println("EXPECTED RESPONSE: ${objectMapper.writeValueAsString(expectedResponse)}")

    webTestClient
        .get()
        .uri(
            "/portfolios/${params.portfolioId}/stocks/${params.stockSymbol}/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  private fun getNumRecordsForInterval(params: CoreSharesOwnedRouteParams): Int =
      when (params.interval) {
        SharesOwnedInterval.SINGLE -> 100
        SharesOwnedInterval.DAILY -> ChronoUnit.DAYS.between(params.startDate, params.endDate) + 10
        SharesOwnedInterval.WEEKLY -> ChronoUnit.DAYS.between(params.startDate, params.endDate) + 10
        SharesOwnedInterval.MONTHLY ->
            ChronoUnit.WEEKS.between(params.startDate, params.endDate) + 10
      }.toInt()

  @MethodSource("sharesOwnedBadRequestParams")
  @ParameterizedTest
  fun `return bad request exceptions for missing parameters for getting shares owned for stock in portfolio`(
      params: SharesOwnedBadRequestParams
  ) {
    val data = createData(10, 100)
    val response =
        ErrorResponse(
            method = "GET",
            uri =
                "/portfolios/${data.portfolios[1].id.value}/stocks/VTI/history?${params.queryString}",
            message = "Bad Request: ${params.message}",
            status = 400)
    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id.value}/stocks/VTI/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isBadRequest
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @MethodSource("sharesOwnedForStock")
  @ParameterizedTest
  fun `get shares owned for stock in all portfolios combined`(
      coreParams: CoreSharesOwnedRouteParams
  ) {
    val numRecords = getNumRecordsForInterval(coreParams)
    val data = createData(10, numRecords)
    val params = coreParams.withKeys(defaultUsers.primaryUser.userTypedId)
    val expectedResponse = createSharesOwnedRouteData(data, params)

    println("EXPECTED RESPONSE: ${objectMapper.writeValueAsString(expectedResponse)}")

    webTestClient
        .get()
        .uri(
            "/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/${params.stockSymbol}/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  @Test
  fun `gets current value of stock in portfolio`() {
    val data = createData(10, 100)

    val maxSharesOwned =
        data.sharesOwned
            .filter { it.portfolioId == data.portfolios[1].id }
            .filter { it.symbol == "VTI" }
            .maxBy { it.dateRangeStart }

    val response =
        SharesOwnedResponse(date = LocalDate.now(), totalShares = maxSharesOwned.totalShares)

    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id}/stocks/VTI/current")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @Test
  fun `gets current value of stock user does not have in portfolio`() {
    val data = createData(10, 100)
    val response = SharesOwnedResponse(date = LocalDate.now(), totalShares = BigDecimal("0"))

    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id}/stocks/ABC/current")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @Test
  fun `gets current value of stock in portfolio not owned by user`() {
    val data = createData(10, 100)
    val response = SharesOwnedResponse(date = LocalDate.now(), totalShares = BigDecimal("0"))

    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[0].id}/stocks/VTI/current")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @Test
  fun `gets current value of stock in all portfolios`() {
    val data = createData(10, 100)

    val totalShares =
        data.sharesOwned
            .asSequence()
            .filter { it.symbol == "VTI" }
            .maxBy { it.dateRangeStart }
            .totalShares

    val response = SharesOwnedResponse(date = LocalDate.now(), totalShares = totalShares)

    webTestClient
        .get()
        .uri("/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/VTI/current")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @Test
  fun `gets current value of stock user does not have in all portfolios`() {
    val data = createData(10, 100)
    val response = SharesOwnedResponse(date = LocalDate.now(), totalShares = BigDecimal("0"))

    webTestClient
        .get()
        .uri("/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/ABC/current")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @MethodSource("sharesOwnedBadRequestParams")
  @ParameterizedTest
  fun `return bad request exceptions for missing parameters for getting shares owned for stock in all portfolios`(
      params: SharesOwnedBadRequestParams
  ) {
    val response =
        ErrorResponse(
            method = "GET",
            uri =
                "/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/VTI/history?${params.queryString}",
            message = "Bad Request: ${params.message}",
            status = 400)
    webTestClient
        .get()
        .uri(
            "/portfolios/${PortfolioConstants.COMBINED_PORTFOLIO_ID}/stocks/VTI/history?${params.queryString}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isBadRequest
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }
}

data class SharesOwnedBadRequestParams(
    val startDate: String?,
    val endDate: String?,
    val interval: String?,
    val message: String
)

val SharesOwnedBadRequestParams.queryString: String
  get() =
      sequenceOf(
              startDate?.let { "startDate=$it" },
              endDate?.let { "endDate=$it" },
              interval?.let { "interval=$it" })
          .filterNotNull()
          .joinToString("&")
