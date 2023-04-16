package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import java.util.UUID

val portfolioRowMapper: RowMapper<Portfolio> = { row ->
  either.eager {
    val id = row.getRequired("id", UUID::class).bind()
    val userId = row.getRequired("user_id", UUID::class).bind()
    val name = row.getRequired("name", String::class).bind()

    BasePortfolio(id = TypedId(id), userId = TypedId(userId), name = name)
  }
}
