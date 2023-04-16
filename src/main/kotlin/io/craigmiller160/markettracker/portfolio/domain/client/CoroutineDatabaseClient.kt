package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.fold
import arrow.typeclasses.Monoid
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.r2dbc.spi.Row
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec

class CoroutineDatabaseClient(private val databaseClient: DatabaseClient) {
  suspend fun query(sql: String, params: Map<String, Any> = mapOf()): TryEither<List<Row>> {
    databaseClient
        .sql(sql)
        .let { executeSpec ->
          params.entries
              .map { (key, value) -> { spec: GenericExecuteSpec -> spec.bind(key, value) } }
              .fold(executeSpecBinderMonoid)
              .let { fn -> fn(executeSpec) }
        }
        .fetch()
        .all()
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

private typealias ExecuteSpecBinder = (GenericExecuteSpec) -> GenericExecuteSpec

private val executeSpecBinderMonoid =
    object : Monoid<ExecuteSpecBinder> {
      override fun empty(): ExecuteSpecBinder = { it }

      override fun ExecuteSpecBinder.combine(b: ExecuteSpecBinder): ExecuteSpecBinder =
          { executeSpec ->
            this(executeSpec).let { b(it) }
          }
    }
