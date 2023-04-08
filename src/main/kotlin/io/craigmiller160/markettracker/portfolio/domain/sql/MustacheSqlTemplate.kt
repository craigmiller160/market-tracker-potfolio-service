package io.craigmiller160.markettracker.portfolio.domain.sql

import arrow.core.Either
import com.github.mustachejava.Mustache
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import java.io.StringWriter

class MustacheSqlTemplate(private val mustache: Mustache) {
  fun executeWithSectionsEnabled(vararg sectionNames: String): TryEither<String> =
      Either.catch {
        val map = sectionNames.associateWith { true }
        StringWriter().also { mustache.execute(it, map) }.toString()
      }
}
