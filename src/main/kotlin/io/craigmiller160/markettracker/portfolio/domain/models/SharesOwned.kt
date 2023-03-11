package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.SharesOwnedId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import java.time.ZonedDateTime

data class SharesOwned(
    val id: TypedId<SharesOwnedId>,
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<PortfolioId>,
    val dateRange: Any, // TODO figure this out
    val symbol: String,
    val totalShares: Double,
    val insertTimestamp: ZonedDateTime,
    val updateTimestamp: ZonedDateTime
)
