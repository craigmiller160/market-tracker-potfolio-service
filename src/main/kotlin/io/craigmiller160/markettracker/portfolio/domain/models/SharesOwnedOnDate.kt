package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.math.BigDecimal
import java.time.LocalDate

data class SharesOwnedOnDate(
    val userId: TypedId<UserId>,
    val date: LocalDate,
    val symbol: String,
    val totalShares: BigDecimal,
    val portfolioId: TypedId<PortfolioId>? = null
)

fun SharesOwnedOnDate.toSharesOwnedResponse(): SharesOwnedResponse =
    SharesOwnedResponse(date = date, totalShares = totalShares)
