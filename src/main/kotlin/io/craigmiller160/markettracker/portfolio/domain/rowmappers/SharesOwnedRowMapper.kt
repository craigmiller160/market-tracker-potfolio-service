package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.getRequired
import java.math.BigDecimal
import java.util.UUID

val sharesOwnedRowMapper: RowMapper<SharesOwned> = { row, _ ->
  either.eager {
    val dateRangeString = row.getRequired("date_range", String::class.java).bind()
    val id = row.getRequired("id", UUID::class.java).bind()
    val userId = row.getRequired("user_id", UUID::class.java).bind()
    val portfolioId = row.getRequired("portfolio_id", UUID::class.java).bind()
    val symbol = row.getRequired("symbol", String::class.java).bind()
    val totalShares = row.getRequired("total_shares", BigDecimal::class.java).bind()

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
