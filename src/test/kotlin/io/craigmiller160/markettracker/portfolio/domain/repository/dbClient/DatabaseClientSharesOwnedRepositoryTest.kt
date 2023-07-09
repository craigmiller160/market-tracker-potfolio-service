package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MarketTrackerPortfolioIntegrationTest
class DatabaseClientSharesOwnedRepositoryTest
@Autowired
constructor(
    private val client: CoroutineDatabaseClient,
    private val repo: DatabaseClientSharesOwnedRepository
) {
  @Test
  fun `gets the current shares owned for stock in portfolio`() {
    TODO()
  }

  @Test
  fun `gets the current shares owned for stock in all portfolios combined`() {
    TODO()
  }

  @Test
  fun `gets the shares owned at an interval for stock in portfolio`() {
    TODO()
  }

  @Test
  fun `gets the shares owned at an interval for stock in all portfolios combined`() {
    TODO()
  }
}
