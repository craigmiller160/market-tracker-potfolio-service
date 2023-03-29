package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.SharesOwnedId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SharesOwned(
    val id: TypedId<SharesOwnedId>,
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<PortfolioId>,
    val dateRangeStart: LocalDate,
    val dateRangeEnd: LocalDate,
    val symbol: String,
    val totalShares: BigDecimal
) {
  companion object
}

val SharesOwned.dateRange: String
  get() = SharesOwned.toDateRange(dateRangeStart, dateRangeEnd)

fun SharesOwned.Companion.toDateRange(dateRangeStart: LocalDate, dateRangeEnd: LocalDate): String {
  val startFormatted = dateRangeStart.format(DateTimeFormatter.ISO_DATE)
  val endFormatted = dateRangeEnd.format(DateTimeFormatter.ISO_DATE)
  return "[$startFormatted,$endFormatted)"
}
