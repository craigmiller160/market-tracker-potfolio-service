package io.craigmiller160.markettracker.portfolio.domain.sql

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither

class MustacheSqlTemplate() {
  fun executeWithSectionsEnabled(vararg sectionNames: String): TryEither<String> =
      Either.catch { TODO("Delete this whole class") }
}
