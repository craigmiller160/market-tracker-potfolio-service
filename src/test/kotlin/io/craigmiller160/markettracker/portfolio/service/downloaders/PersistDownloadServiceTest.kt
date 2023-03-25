package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne

@MarketTrackerPortfolioIntegrationTest
class PersistDownloadServiceTest
@Autowired
constructor(
    private val databaseClient: DatabaseClient,
    private val persistDownloadService: PersistDownloadService
) {
  companion object {
    private val PORTFOLIO_ID = TypedId<PortfolioId>()
    private val USER_ID = TypedId<UserId>()
    private val DATA: List<PortfolioWithHistory> =
        listOf(
            PortfolioWithHistory(
                id = PORTFOLIO_ID,
                userId = USER_ID,
                name = "Portfolio",
                ownershipHistory =
                    listOf(
                        SharesOwned(
                            id = TypedId(),
                            portfolioId = PORTFOLIO_ID,
                            userId = USER_ID,
                            dateRangeStart = LocalDate.of(2022, 1, 1),
                            dateRangeEnd = LocalDate.of(2022, 2, 1),
                            symbol = "VTI",
                            totalShares = BigDecimal("10")))))
  }
  @Test
  fun `the portfolios are all persisted`() {
    runBlocking {
      getPortfolios().shouldBeEqualComparingTo(0)
      getSharesOwnedCount().shouldBeEqualComparingTo(0)

      persistDownloadService.persistPortfolios(DATA).getOrThrow()
    }
  }

  private suspend fun getPortfolios(): Long =
      databaseClient.sql("SELECT * FROM portfolios").fetch().awaitOne()["count"] as Long

  private suspend fun getSharesOwnedCount(): Long =
      databaseClient.sql("SELECT * FROM shares_owned").fetch().awaitOne()["count"] as Long
}
