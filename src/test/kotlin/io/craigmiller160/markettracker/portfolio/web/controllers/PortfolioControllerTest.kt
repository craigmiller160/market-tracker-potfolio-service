package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class PortfolioControllerTest
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    private val portfolioRepo: PortfolioRepository,
    private val defaultUsers: DefaultUsers
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
    TODO("Create the portfolios in the db")
  }
  @Test
  fun `gets list of portfolio names for user`() {
    webTestClient
        .get()
        .uri("/portfolios/names")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
    TODO()
  }
}
