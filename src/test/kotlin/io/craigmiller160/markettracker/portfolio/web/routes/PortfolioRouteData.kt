package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MAX
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
val BASE_DATE = LocalDate.of(2022, 1, 1)

fun createPortfolioRouteData(
    defaultUsers: DefaultUsers,
    offsetDays: Int,
    numRecords: Int
): PortfolioRouteData {
  val portfolios = createPortfolios(defaultUsers)
  val sharesOwned = createSharesOwned(portfolios, offsetDays, numRecords)
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

private fun createSharesOwned(
    portfolios: List<Portfolio>,
    offsetDays: Int,
    numRecords: Int
): List<SharesOwned> =
    portfolios.flatMap { portfolio ->
      STOCKS.flatMap { symbol ->
        (0 until numRecords).map { index ->
          val dateOffset = offsetDays.toLong() * index
          SharesOwned(
              id = TypedId(),
              portfolioId = portfolio.id,
              userId = portfolio.userId,
              dateRangeStart = BASE_DATE.plusDays(dateOffset),
              dateRangeEnd =
                  if (index < numRecords - 1) {
                    BASE_DATE.plusDays(dateOffset + offsetDays)
                  } else {
                    DATE_RANGE_MAX
                  },
              symbol = symbol,
              totalShares = BigDecimal("${index + 1}"))
        }
      }
    }
