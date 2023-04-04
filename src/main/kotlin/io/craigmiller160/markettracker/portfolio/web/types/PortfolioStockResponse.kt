package io.craigmiller160.markettracker.portfolio.web.types

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId

data class PortfolioStockResponse(
    val portfolioId: TypedId<PortfolioId>,
    val portfolioName: String,
    val stockSymbols: List<String>
)
