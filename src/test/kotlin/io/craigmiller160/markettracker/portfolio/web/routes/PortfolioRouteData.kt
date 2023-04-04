package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import java.math.BigDecimal
import java.time.LocalDate

data class PortfolioRouteData(
    val uniqueStocks: List<String>,
    val portfolios: List<Portfolio>,
    val sharesOwned: List<SharesOwned>
)

private val STOCKS: List<String> = listOf("VTI", "VXUS", "VOO")
private val BASE_DATE = LocalDate.now()
private const val DATE_RANGE_LENGTH = 10L

fun createPortfolioRouteData(defaultUsers: DefaultUsers): PortfolioRouteData {
  val portfolios = createPortfolios(defaultUsers)
  val sharesOwned = createSharesOwned(portfolios)
  return PortfolioRouteData(
      uniqueStocks = STOCKS, portfolios = portfolios, sharesOwned = sharesOwned)
}

private fun createPortfolios(defaultUsers: DefaultUsers): List<Portfolio> =
    (0 until 5).map { index ->
      BasePortfolio(
          id = TypedId(),
          userId =
              if (index == 0) TypedId(defaultUsers.secondaryUser.userId)
              else TypedId(defaultUsers.primaryUser.userId),
          name = "Portfolio-$index")
    }

private fun createSharesOwned(portfolios: List<Portfolio>): List<SharesOwned> =
    portfolios.flatMapIndexed { portfolioIndex, portfolio ->
      STOCKS.flatMap { symbol ->
        (0 until 100).map { index ->
          val dateOffset = DATE_RANGE_LENGTH * index
          SharesOwned(
              id = TypedId(),
              portfolioId = portfolio.id,
              userId = portfolio.userId,
              dateRangeStart = BASE_DATE.plusDays(dateOffset),
              dateRangeEnd = BASE_DATE.plusDays(dateOffset + DATE_RANGE_LENGTH),
              symbol = symbol,
              totalShares = BigDecimal("${index + 1}"))
        }
      }
    }
