package io.craigmiller160.markettracker.portfolio.domain.sql

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import java.io.Reader
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class SqlLoader(private val resourceLoader: ResourceLoader) {
  companion object {
    const val SQL_CACHE = "SQL_CACHE"
    const val MUSTACHE_CACHE = "MUSTACHE_CACHE"
  }

  private fun openSqlReader(filePath: String): Reader =
      resourceLoader.getResource("classpath:sql/$filePath").inputStream.reader()

  @Cacheable(cacheNames = [SQL_CACHE], key = "#filePath")
  fun loadSql(filePath: String): TryEither<String> =
      Either.catch { openSqlReader(filePath).readText() }
}
