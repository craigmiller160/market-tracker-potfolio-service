package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.matchers.collections.shouldBeEmpty
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient

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
      getPortfolios().shouldBeEmpty()
      getSharesOwned().shouldBeEmpty()

      persistDownloadService.persistPortfolios(DATA).getOrThrow()
      TODO()
    }
  }

  private suspend fun getPortfolios(): List<Portfolio> {
    databaseClient.sql("SELECT * FROM portfolios").map { a, b -> a }.all()
    TODO()
  }
  private suspend fun getSharesOwned(): List<SharesOwned> {
    databaseClient.sql("SELECT * FROM shares_owned").fetch().all()
    TODO()
  }
}
