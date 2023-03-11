package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.domain.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.domain.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.typedid.UserId
import java.time.ZonedDateTime

data class Portfolio(
    val id: TypedId<PortfolioId>,
    val userId: TypedId<UserId>,
    val name: String,
    val insertTimestamp: ZonedDateTime,
    val updateTimestamp: ZonedDateTime
)
