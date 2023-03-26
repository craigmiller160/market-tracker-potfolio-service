package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.portfolioRowMapper
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.sharesOwnedRowMapper
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
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
                            totalShares = BigDecimal("10")),
                        SharesOwned(
                            id = TypedId(),
                            portfolioId = PORTFOLIO_ID,
                            userId = USER_ID,
                            dateRangeStart = LocalDate.of(2022, 2, 1),
                            dateRangeEnd = LocalDate.of(2022, 3, 1),
                            symbol = "VTI",
                            totalShares = BigDecimal("20")))))
  }
  @Test
  fun `the portfolios are all persisted`() {
    runBlocking {
      getPortfolios().toList().shouldHaveSize(0)
      getSharesOwned().toList().shouldHaveSize(0)

      persistDownloadService.persistPortfolios(DATA).getOrThrow()

      val portfolios = getPortfolios().toList()
      portfolios.shouldHaveSize(1)
      portfolios[0].let { portfolio ->
        portfolio.id.shouldBeEqualToComparingFields(DATA[0].id)
        portfolio.userId.shouldBeEqualToComparingFields(DATA[0].userId)
        portfolio.name.shouldBeEqualToComparingFields(DATA[0].name)
      }

      val sharesOwned = getSharesOwned().toList()
      sharesOwned.shouldHaveSize(2)
      sharesOwned.forEachIndexed { index, actual ->
        actual.id.shouldBeEqualToComparingFields(DATA[0].ownershipHistory[index].id)
        actual.userId.shouldBeEqualToComparingFields(DATA[0].ownershipHistory[index].userId)
        actual.portfolioId.shouldBeEqualToComparingFields(
            DATA[0].ownershipHistory[index].portfolioId)
        actual.dateRangeStart.shouldBeEqualToComparingFields(
            DATA[0].ownershipHistory[index].dateRangeStart)
        actual.dateRangeEnd.shouldBeEqualToComparingFields(
            DATA[0].ownershipHistory[index].dateRangeEnd)
        actual.symbol.shouldBeEqualToComparingFields(DATA[0].ownershipHistory[index].symbol)
        actual.totalShares.shouldBeEqualToComparingFields(
            DATA[0].ownershipHistory[index].totalShares)
      }
    }
  }

  private suspend fun getPortfolios(): Flow<Portfolio> =
      databaseClient.sql("SELECT * FROM portfolios").map(portfolioRowMapper).all().asFlow()
  private suspend fun getSharesOwned(): Flow<SharesOwned> =
      databaseClient.sql("SELECT * FROM shares_owned").map(sharesOwnedRowMapper).all().asFlow()
}
