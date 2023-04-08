package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import java.math.BigDecimal
import java.time.LocalDate

data class PortfolioSharesOwnedOnDate(
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<UserId>,
    val date: LocalDate,
    val symbol: String,
    val totalShares: BigDecimal
)
