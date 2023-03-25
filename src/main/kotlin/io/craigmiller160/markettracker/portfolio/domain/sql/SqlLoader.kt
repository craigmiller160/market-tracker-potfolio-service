package io.craigmiller160.markettracker.portfolio.domain.sql

import com.github.michaelbull.result.map
import com.github.mustachejava.DefaultMustacheFactory
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
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
  private val mustacheFactory = DefaultMustacheFactory()

  private fun openSqlReader(filePath: String): Reader =
      resourceLoader.getResource("classpath:sql/$filePath").inputStream.reader()

  @Cacheable(cacheNames = [SQL_CACHE], key = "#filePath")
  fun loadSql(filePath: String): KtResult<String> = ktRunCatching {
    openSqlReader(filePath).readText()
  }

  @Cacheable(cacheNames = [MUSTACHE_CACHE], key = "#filePath")
  fun loadSqlMustacheTemplate(filePath: String): KtResult<MustacheSqlTemplate> =
      ktRunCatching { mustacheFactory.compile(openSqlReader(filePath), filePath) }
          .map { MustacheSqlTemplate(it) }
}
