package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.getRequired
import java.util.UUID

val portfolioRowMapper: RowMapper<Portfolio> = { row, _ ->
  either.eager {
    val id = row.getRequired("id", UUID::class.java).bind()
    val userId = row.getRequired("user_id", UUID::class.java).bind()
    val name = row.getRequired("name", String::class.java).bind()

    BasePortfolio(id = TypedId(id), userId = TypedId(userId), name = name)
  }
}
