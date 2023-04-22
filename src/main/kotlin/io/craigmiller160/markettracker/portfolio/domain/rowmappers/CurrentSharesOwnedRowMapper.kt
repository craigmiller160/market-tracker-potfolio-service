package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import java.math.BigDecimal

val currentSharesOwnedRowMapper: RowMapper<BigDecimal> = { row ->
  row.getOptional("total_shares", BigDecimal::class).map { it ?: BigDecimal("0") }
}
