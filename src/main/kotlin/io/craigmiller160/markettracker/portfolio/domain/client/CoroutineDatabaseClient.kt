package io.craigmiller160.markettracker.portfolio.domain.client

import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.r2dbc.spi.Row
import org.springframework.r2dbc.core.DatabaseClient

class CoroutineDatabaseClient(private val databaseClient: DatabaseClient) {
  suspend fun query(sql: String, params: Map<String, Any> = mapOf()): TryEither<List<Row>> {
    TODO()
  }

  suspend fun update(sql: String, params: Map<String, Any> = mapOf()): TryEither<Long> {
    TODO()
  }

  suspend fun batchUpdate(
      sql: String,
      params: List<Map<String, Any>> = listOf()
  ): TryEither<List<Long>> {
    TODO()
  }
}
