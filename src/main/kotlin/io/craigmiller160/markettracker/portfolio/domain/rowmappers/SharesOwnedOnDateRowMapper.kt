package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

val sharesOwnedOnDateRowMapper: RowMapper<SharesOwnedOnDate> = { row ->
  either {
    val userId = row.getRequired("user_id", UUID::class).bind()
    val date = row.getRequired("date", LocalDate::class).bind()
    val symbol = row.getRequired("symbol", String::class).bind()
    val totalShares = row.getRequired("total_shares", BigDecimal::class).bind()
    val portfolioId = row.getOptional("portfolio_id", UUID::class).bind()

    SharesOwnedOnDate(
        userId = TypedId(userId),
        date = date,
        symbol = symbol,
        totalShares = totalShares,
        portfolioId = portfolioId?.let { TypedId(it) })
  }
}
