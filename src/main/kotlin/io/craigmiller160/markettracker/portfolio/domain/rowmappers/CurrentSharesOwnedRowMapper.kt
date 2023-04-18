package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import java.math.BigDecimal

val currentSharesOwnedRowMapper: RowMapper<BigDecimal> = { row ->
  row.getRequired("the_sum", BigDecimal::class)
}
