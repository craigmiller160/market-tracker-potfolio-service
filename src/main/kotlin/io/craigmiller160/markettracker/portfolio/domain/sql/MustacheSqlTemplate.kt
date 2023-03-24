package io.craigmiller160.markettracker.portfolio.domain.sql

import com.github.mustachejava.Mustache
import java.io.StringWriter

class MustacheSqlTemplate(private val mustache: Mustache) {
  fun executeWithSectionsEnabled(vararg sectionNames: String): String {
    val map = sectionNames.associateWith { true }
    return StringWriter().also { mustache.execute(it, map) }.toString()
  }
}
