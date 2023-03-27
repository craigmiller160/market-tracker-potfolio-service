package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import java.math.BigDecimal
import java.util.UUID

val sharesOwnedRowMapper: RowMapper<SharesOwned> = { row, metadata ->
  either.eager {
    // TODO handle type exceptions
    val dateRangeString =
        row.get("date_range", String::class.java).leftIfNull("Missing date_range column").bind()
    val id = row.get("id", UUID::class.java).leftIfNull("Missing id column").bind()
    val userId = row.get("user_id", UUID::class.java).leftIfNull("Missing user_id column").bind()
    val portfolioId =
        row.get("portfolio_id", UUID::class.java).leftIfNull("Missing portfolio_id column").bind()
    val symbol = row.get("symbol", String::class.java).leftIfNull("Missing symbol column").bind()
    val totalShares =
        row.get("total_shares", BigDecimal::class.java)
            .leftIfNull("Missing total_shares column")
            .bind()

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
