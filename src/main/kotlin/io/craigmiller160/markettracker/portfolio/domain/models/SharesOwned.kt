package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.domain.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.domain.typedid.SharesOwnedId
import io.craigmiller160.markettracker.portfolio.domain.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.typedid.UserId

data class SharesOwned(
    val id: TypedId<SharesOwnedId>,
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<PortfolioId>,
    val dateRange: Any, // TODO figure this out
    val symbol: String,
    val totalShares: Double
)
