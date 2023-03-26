package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import java.util.UUID

val portfolioRowMapper: RowMapper<Portfolio> = { row, metadata ->
  ktRunCatching {
    BasePortfolio(
        id = TypedId(row.get("id", UUID::class.java)!!),
        userId = TypedId(row.get("user_id", UUID::class.java)!!),
        name = row.get("name", String::class.java)!!)
  }
}
