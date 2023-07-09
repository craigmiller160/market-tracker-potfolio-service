package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.getOrElse
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import java.math.BigDecimal
import java.time.LocalDate
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
    private const val STOCK = "VTI"
  }

  // TODO add to tests to ensure that other user data isn't pulled out

  @BeforeEach
  fun setup() {
    runBlocking {
      portfolioRepo.createPortfolio(
          BasePortfolio(id = PORTFOLIO_ID, userId = USER_ID, name = "MyPortfolio"))
    }
  }

  private fun createSharesOwned(
      start: LocalDate,
      end: LocalDate,
      totalShares: BigDecimal,
      portfolioId: TypedId<PortfolioId> = PORTFOLIO_ID
  ): SharesOwned = runBlocking {
    sharesOwnedRepo
        .createAllSharesOwned(
            listOf(
                SharesOwned(
                    id = TypedId(),
                    portfolioId = portfolioId,
                    userId = USER_ID,
                    dateRangeStart = start,
                    dateRangeEnd = end,
                    symbol = STOCK,
                    totalShares = totalShares)))
        .getOrElse { throw it }
        .first()
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
