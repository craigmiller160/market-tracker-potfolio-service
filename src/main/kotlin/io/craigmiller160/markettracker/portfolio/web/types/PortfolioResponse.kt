package io.craigmiller160.markettracker.portfolio.web.types

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId

data class PortfolioResponse(
    val id: TypedId<PortfolioId>,
    val name: String,
    val stockSymbols: List<String>
)
