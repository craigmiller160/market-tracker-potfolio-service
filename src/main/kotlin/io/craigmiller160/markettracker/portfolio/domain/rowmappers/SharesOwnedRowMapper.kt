package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import java.math.BigDecimal
import java.util.UUID

val sharesOwnedRowMapper: RowMapper<SharesOwned> = { row ->
  either {
    val dateRangeString = row.getRequired("date_range", String::class).bind()
    val id = row.getRequired("id", UUID::class).bind()
    val userId = row.getRequired("user_id", UUID::class).bind()
    val portfolioId = row.getRequired("portfolio_id", UUID::class).bind()
    val symbol = row.getRequired("symbol", String::class).bind()
    val totalShares = row.getRequired("total_shares", BigDecimal::class).bind()

    val (dateRangeStart, dateRangeEnd) = dateRangeTypeParser(dateRangeString).bind()

    SharesOwned(
        id = TypedId(id),
        userId = TypedId(userId),
        portfolioId = TypedId(portfolioId),
        dateRangeStart = dateRangeStart,
        dateRangeEnd = dateRangeEnd,
        symbol = symbol,
        totalShares = totalShares)
  }
}
