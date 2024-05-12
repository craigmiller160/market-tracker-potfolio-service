package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId

object PortfolioConstants {
  val PORTFOLIO_TOTALS_INDIVIDUAL_STOCKS_ID =
      TypedId<PortfolioId>("f56f0db2-e77d-43b8-a466-94825ea8db2a")
  const val PORTFOLIO_TOTALS_INDIVIDUAL_STOCKS_TITLE = "Portfolio Totals, Individual Stocks"
  val PORTFOLIO_TOTALS_ID = TypedId<PortfolioId>("8da983bf-ca53-4631-b417-00b3012a5843")
  const val PORTFOLIO_TOTALS_TITLE = "Portfolio Totals"
}
