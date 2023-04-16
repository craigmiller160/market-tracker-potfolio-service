package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
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
      insertPortfolio(USER_ID).let { insertSharesOwned(it) }
      getPortfolios().shouldHaveSize(1)
      getSharesOwned().shouldHaveSize(1)

      persistDownloadService.persistPortfolios(DATA).shouldBeRight()

      val portfolios = getPortfolios()
      portfolios.shouldHaveSize(1)
      portfolios[0].let { portfolio ->
        portfolio.id.shouldBeEqualToComparingFields(DATA[0].id)
        portfolio.userId.shouldBeEqualToComparingFields(DATA[0].userId)
        portfolio.name.shouldBeEqualToComparingFields(DATA[0].name)
      }

      val sharesOwned = getSharesOwned()
      sharesOwned.shouldHaveSize(2)
      sharesOwned.forEachIndexed { index, actual ->
        actual.id.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].id)
        actual.userId.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].userId)
        actual.portfolioId.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].portfolioId)
        actual.dateRangeStart.shouldBeEqualComparingTo(
            DATA[0].ownershipHistory[index].dateRangeStart)
        actual.dateRangeEnd.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].dateRangeEnd)
        actual.symbol.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].symbol)
        actual.totalShares.shouldBeEqualComparingTo(DATA[0].ownershipHistory[index].totalShares)
      }
    }
  }

  private suspend fun insertPortfolio(userId: TypedId<UserId>): Portfolio {
    val portfolio = BasePortfolio(id = TypedId(), userId = userId, name = "TestPortfolio")
    databaseClient
        .sql("INSERT INTO portfolios (id, user_id, name) VALUES (:id, :userId, :name)")
        .bind("id", portfolio.id.value)
        .bind("userId", portfolio.userId.value)
        .bind("name", portfolio.name)
        .fetch()
        .rowsUpdated()
        .awaitSingle()
    return portfolio
  }
  private suspend fun insertSharesOwned(portfolio: Portfolio) =
      databaseClient
          .sql(
              "INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares) VALUES (:id, :userId, :portfolioId, :dateRange::daterange, :symbol, :totalShares)")
          .bind("id", UUID.randomUUID())
          .bind("userId", portfolio.userId.value)
          .bind("portfolioId", portfolio.id.value)
          .bind("symbol", "VTI")
          .bind("totalShares", BigDecimal("1"))
          .bind("dateRange", "[2022-01-01,2022-01-02)")
          .fetch()
          .rowsUpdated()
          .awaitSingle()

  private suspend fun getPortfolios(): List<Portfolio> = TODO()
  //      databaseClient
  //          .sql("SELECT * FROM portfolios")
  //          .map(portfolioRowMapper)
  //          .all()
  //          .asFlow()
  //          .toList()
  //          .sequence()
  //          .shouldBeRight()
  private suspend fun getSharesOwned(): List<SharesOwned> = TODO()
  //      databaseClient
  //          .sql("SELECT * FROM shares_owned")
  //          .map(sharesOwnedRowMapper)
  //          .all()
  //          .asFlow()
  //          .toList()
  //          .sequence()
  //          .shouldBeRight()
}
