package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import io.craigmiller160.markettracker.portfolio.extensions.getOptional
import io.craigmiller160.markettracker.portfolio.extensions.getRequired
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

val sharesOwnedOnDateRowMapper: RowMapper<SharesOwnedOnDate> = { row, _ ->
  either.eager {
    val userId = row.getRequired("user_id", UUID::class.java).bind()
    val date = row.getRequired("date", LocalDate::class.java).bind()
    val symbol = row.getRequired("symbol", String::class.java).bind()
    val totalShares = row.getRequired("total_shares", BigDecimal::class.java).bind()
    val portfolioId = row.getOptional("portfolio_id", UUID::class.java).bind()

    SharesOwnedOnDate(
        userId = TypedId(userId),
        date = date,
        symbol = symbol,
        totalShares = totalShares,
        portfolioId = portfolioId?.let { TypedId(it) })
  }
}
