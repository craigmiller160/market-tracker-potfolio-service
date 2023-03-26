package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import com.github.michaelbull.result.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import java.math.BigDecimal
import java.util.UUID

val sharesOwnedRowMapper: RowMapper<SharesOwned> = { row, metadata ->
  dateRangeTypeParser(row.get("date_range", String::class.java)!!).flatMap {
      (dateRangeStart, dateRangeEnd) ->
    ktRunCatching {
      SharesOwned(
          id = TypedId(row.get("id", UUID::class.java)!!),
          userId = TypedId(row.get("user_id", UUID::class.java)!!),
          portfolioId = TypedId(row.get("portfolio_id", UUID::class.java)!!),
          dateRangeStart = dateRangeStart,
          dateRangeEnd = dateRangeEnd,
          symbol = row.get("symbol", String::class.java)!!,
          totalShares = row.get("total_shares", BigDecimal::class.java)!!)
    }
  }
}
