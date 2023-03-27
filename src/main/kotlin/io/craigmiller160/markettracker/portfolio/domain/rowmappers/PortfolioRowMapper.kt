package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import java.util.UUID

val portfolioRowMapper: RowMapper<Portfolio> = { row, metadata ->
  either.eager {
    row.get("id", UUID::class.java).leftIfNull("Missing id column").bind()
    row.get("user_id", UUID::class.java).leftIfNull("Missing user_id column").bind()
    row.get("name", String::class.java).leftIfNull("Missing name column").bind()
  }

  Either.catch {
    BasePortfolio(
        id = TypedId(row.get("id", UUID::class.java)!!),
        userId = TypedId(row.get("user_id", UUID::class.java)!!),
        name = row.get("name", String::class.java)!!)
  }
}
