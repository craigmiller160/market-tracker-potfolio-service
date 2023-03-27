package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import java.util.UUID

val portfolioRowMapper: RowMapper<Portfolio> = { row, metadata ->
  either.eager {
    // TODO handle type exceptions
    val id = row.get("id", UUID::class.java).leftIfNull("Missing id column").bind()
    val userId = row.get("user_id", UUID::class.java).leftIfNull("Missing user_id column").bind()
    val name = row.get("name", String::class.java).leftIfNull("Missing name column").bind()

    BasePortfolio(id = TypedId(id), userId = TypedId(userId), name = name)
  }
}
