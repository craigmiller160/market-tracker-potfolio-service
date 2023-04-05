package io.craigmiller160.markettracker.portfolio.web.routes

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioNameResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import java.util.stream.Stream
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
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
    @JvmStatic fun sharesOwnedForStockInPortfolio(): Stream<Any> = TODO()

    @JvmStatic fun sharesOwnedForStockInCombinedPortfolios(): Stream<Any> = TODO()
  }

  private val data = createPortfolioRouteData(defaultUsers)

  @BeforeEach
  fun setup() {
    runBlocking {
      portfolioRepo.createAllPortfolios(data.portfolios)
      sharesOwnedRepo.createAllSharesOwned(data.sharesOwned)
    }
  }
  @Test
  fun `gets list of portfolio names for user`() {
    val expectedResponse = data.portfolios.drop(1).map { it.toPortfolioNameResponse() }
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

  @Test
  fun `get shares owned history for past week for stock in portfolio not owned by user`() {
    TODO()
  }

  @MethodSource("sharesOwnedForStockInPortfolio")
  @ParameterizedTest
  fun `get shares owned for stock in portfolio`() {
    TODO()
  }

  @Test
  fun `return bad request exceptions for missing parameters for getting shares owned for stock in portfolio`() {
    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id.value}/VTI/shares")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isBadRequest
    TODO()
  }

  @MethodSource("sharesOwnedForStockInCombinedPortfolios")
  @Test
  fun `get shares owned for stock in all portfolios`() {
    TODO()
  }

  @Test
  fun `return bad request exceptions for missing parameters for getting shares owned for stock in all portfolios`() {
    webTestClient
        .get()
        .uri("/portfolios/${data.portfolios[1].id.value}/combined/shares")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isBadRequest
    TODO()
  }
}
