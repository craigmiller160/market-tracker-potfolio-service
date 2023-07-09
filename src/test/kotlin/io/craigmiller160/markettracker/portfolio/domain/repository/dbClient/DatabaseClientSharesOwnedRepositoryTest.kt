package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
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
    private val USER_1_ID = TypedId<UserId>()
    private val USER_2_ID = TypedId<UserId>()
    private val PORTFOLIO_1_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_2_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_3_ID = TypedId<PortfolioId>()
    private const val STOCK = "VTI"
  }

  // TODO add to tests to ensure that other user data isn't pulled out

  @BeforeEach fun setup() {}

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
