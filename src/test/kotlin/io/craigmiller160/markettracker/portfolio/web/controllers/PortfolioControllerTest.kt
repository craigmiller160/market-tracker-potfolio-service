package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class PortfolioControllerTest
@Autowired
constructor(private val webTestClient: WebTestClient, private val defaultUsers: DefaultUsers) {
  @Test
  fun `gets list of portfolio names for user`() {
    TODO()
  }
}
