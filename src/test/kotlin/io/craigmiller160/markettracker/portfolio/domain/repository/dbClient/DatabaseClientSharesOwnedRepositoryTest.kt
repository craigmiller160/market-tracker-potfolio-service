package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MarketTrackerPortfolioIntegrationTest
class DatabaseClientSharesOwnedRepositoryTest
@Autowired
constructor(
    private val client: CoroutineDatabaseClient,
    private val sharesOwnedRepo: DatabaseClientSharesOwnedRepository,
    private val portfolioRepo: DatabaseClientPortfolioRepository
) {
  companion object {
    private val PORTFOLIO_ID = TypedId<PortfolioId>()
    private val USER_ID = TypedId<UserId>()
  }

  @BeforeEach
  fun setup() {
    runBlocking {
      portfolioRepo.createPortfolio(
          BasePortfolio(id = PORTFOLIO_ID, userId = USER_ID, name = "MyPortfolio"))
    }
  }

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
