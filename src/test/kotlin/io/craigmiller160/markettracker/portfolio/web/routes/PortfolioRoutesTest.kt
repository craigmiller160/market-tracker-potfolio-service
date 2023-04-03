package io.craigmiller160.markettracker.portfolio.web.routes

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioNameResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioStockResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class PortfolioRoutesTest
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    private val portfolioRepo: PortfolioRepository,
    private val defaultUsers: DefaultUsers,
    private val objectMapper: ObjectMapper
) {

  private val portfolios =
      (0 until 5).map { index ->
        BasePortfolio(
            id = TypedId(),
            userId =
                if (index == 0) TypedId(defaultUsers.secondaryUser.userId)
                else TypedId(defaultUsers.primaryUser.userId),
            name = "Portfolio-$index")
      }

  @BeforeEach
  fun setup() {
    runBlocking { portfolioRepo.createPortfolios(portfolios) }
  }
  @Test
  fun `gets list of portfolio names for user`() {
    val expectedResponse = portfolios.drop(1).map { it.toPortfolioNameResponse() }
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
    TODO()
  }

  @Test
  fun `gets list of stocks in portfolio that the user does not own`() {
    webTestClient
        .get()
        .uri("/portfolios/${portfolios[0].id}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<PortfolioStockResponse>()))
  }
}
