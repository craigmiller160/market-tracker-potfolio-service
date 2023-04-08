package io.craigmiller160.markettracker.portfolio.web.routes

import arrow.core.flatMap
import arrow.core.getOrElse
import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
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
    private const val DATE_RANGE_LENGTH = 10L
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
  @Test
  fun `gets list of portfolio names for user`() {
    val data = createData(10, 100)
    val expectedResponse = data.portfolios.drop(1).map { it.toPortfolioResponse() }
    webTestClient
        .get()
        .uri("/portfolios")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  @Test
  fun `gets list of stocks in portfolio for user`() {
    val data = createData(10, 100)
    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(data.uniqueStocks))
  }

  @Test
  fun `gets list of stocks in portfolio that the user does not own`() {
    val data = createData(10, 100)
    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[0].id}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<String>()))
  }

  @Test
  fun `gets a list of unique stocks for all portfolios combined`() {
    val data = createData(10, 100)
    val expectedResponse =
        data.uniqueStocks
            .flatMap { stock ->
              (1..4).map { index ->
                if (index % 2 == 0) {
                  stock
                } else {
                  "$stock-$index"
                }
              }
            }
            .distinct()
            .sorted()
    webTestClient
        .get()
        .uri("/portfolios/combined")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(data.uniqueStocks))
  }
}
