package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import java.math.BigDecimal

val currentSharesOwnedRowMapper: RowMapper<BigDecimal> = { row ->
  row.getOptional("the_sum", BigDecimal::class).map { it ?: BigDecimal("0") }
}
