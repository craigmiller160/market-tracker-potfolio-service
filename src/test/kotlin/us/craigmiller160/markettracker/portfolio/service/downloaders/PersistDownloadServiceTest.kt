package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.portfolioRowMapper
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.sharesOwnedRowMapper
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MarketTrackerPortfolioIntegrationTest
class PersistDownloadServiceTest
@Autowired
constructor(
    private val databaseClient: CoroutineDatabaseClient,
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
        actual.id.shouldBe(DATA[0].ownershipHistory[index].id)
        actual.userId.shouldBe(DATA[0].ownershipHistory[index].userId)
        actual.portfolioId.shouldBe(DATA[0].ownershipHistory[index].portfolioId)
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
    val params =
        mapOf(
            "id" to portfolio.id.value,
            "userId" to portfolio.userId.value,
            "name" to portfolio.name)
    databaseClient
        .update("INSERT INTO portfolios (id, user_id, name) VALUES (:id, :userId, :name)", params)
        .shouldBeRight()
    return portfolio
  }
  private suspend fun insertSharesOwned(portfolio: Portfolio) {
    val params =
        mapOf(
            "id" to UUID.randomUUID(),
            "userId" to portfolio.userId.value,
            "portfolioId" to portfolio.id.value,
            "symbol" to "VTI",
            "totalShares" to BigDecimal("1"),
            "dateRange" to "[2022-01-01,2022-01-02)")
    databaseClient
        .update(
            "INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares) VALUES (:id, :userId, :portfolioId, :dateRange::daterange, :symbol, :totalShares)",
            params)
        .shouldBeRight()
  }

  private suspend fun getPortfolios(): List<Portfolio> =
      databaseClient
          .query("SELECT * FROM portfolios")
          .flatMap { list -> list.map(portfolioRowMapper).bindToList() }
          .shouldBeRight()
  private suspend fun getSharesOwned(): List<SharesOwned> =
      databaseClient
          .query("SELECT * FROM shares_owned")
          .flatMap { list -> list.map(sharesOwnedRowMapper).bindToList() }
          .shouldBeRight()
}
