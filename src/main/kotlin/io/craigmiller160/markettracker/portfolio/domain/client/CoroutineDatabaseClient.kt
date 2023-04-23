package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.fold
import arrow.core.sequence
import arrow.typeclasses.Monoid
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.RowMapper
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.r2dbc.spi.Statement
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec
import reactor.kotlin.core.publisher.toFlux

class CoroutineDatabaseClient(private val databaseClient: DatabaseClient) {
  suspend fun query(sql: String, params: Map<String, Any> = mapOf()): TryEither<List<Row>> =
      Either.catch {
        databaseClient
            .sql(sql)
            .let { executeSpec -> paramsToExecuteSpecBinder(params).let { fn -> fn(executeSpec) } }
            .fetch()
            .all()
            .asFlow()
            .map { Row(it) }
            .toList()
      }

  suspend fun <T> query(
      sql: String,
      rowMapper: RowMapper<T>,
      params: Map<String, Any> = mapOf()
  ): TryEither<List<T>> = query(sql, params).flatMap { list -> list.map(rowMapper).sequence() }

  suspend fun update(sql: String, params: Map<String, Any> = mapOf()): TryEither<Long> =
      Either.catch {
        databaseClient
            .sql(sql)
            .let { executeSpec -> paramsToExecuteSpecBinder(params).let { fn -> fn(executeSpec) } }
            .fetch()
            .rowsUpdated()
            .awaitSingle()
      }

  @OptIn(kotlinx.coroutines.FlowPreview::class)
  suspend fun batchUpdate(
      sql: String,
      paramBatches: List<List<Any>> = listOf()
  ): TryEither<List<Long>> {
    if (paramBatches.isEmpty()) {
      return Either.Right(listOf())
    }

    return Either.catch {
      databaseClient
          .inConnectionMany { conn ->
            conn
                .createStatement(sql)
                .let { statement ->
                  paramBatchesToStatementBinder(paramBatches).let { fn -> fn(statement) }
                }
                .execute()
                .toFlux()
          }
          .asFlow()
          .flatMapConcat { result -> result.rowsUpdated.asFlow() }
          .toList()
    }
  }
}

private typealias StatementBinder = (Statement) -> Statement

private val statementBinderMonoid =
    object : Monoid<StatementBinder> {
      override fun empty(): StatementBinder = { it }
      override fun StatementBinder.combine(b: StatementBinder): StatementBinder = { stmt ->
        this(stmt).let(b)
      }
    }

private val statementBatchBinderMonoid =
    object : Monoid<StatementBinder> {
      override fun empty(): StatementBinder = { it }
      override fun StatementBinder.combine(b: StatementBinder): StatementBinder = { stmt ->
        this(stmt).add()
        b(stmt)
      }
    }

private fun paramsToStatementBinder(params: List<Any>): StatementBinder =
    params
        .mapIndexed { index, value ->
          { stmt: Statement ->
            when (value) {
              is NullValue<*> -> stmt.bindNull(index, value.type.java)
              else -> stmt.bind(index, value)
            }
          }
        }
        .fold(statementBinderMonoid)

private fun paramBatchesToStatementBinder(paramBatches: List<List<Any>>): StatementBinder =
    paramBatches
        .map { params ->
          { stmt: Statement -> paramsToStatementBinder(params).let { fn -> fn(stmt) } }
        }
        .fold(statementBatchBinderMonoid)

private typealias ExecuteSpecBinder = (GenericExecuteSpec) -> GenericExecuteSpec

private val executeSpecBinderMonoid =
    object : Monoid<ExecuteSpecBinder> {
      override fun empty(): ExecuteSpecBinder = { it }
      override fun ExecuteSpecBinder.combine(b: ExecuteSpecBinder): ExecuteSpecBinder =
          { executeSpec ->
            this(executeSpec).let(b)
          }
    }

private fun paramsToExecuteSpecBinder(params: Map<String, Any>): ExecuteSpecBinder =
    params.entries
        .map { (key, value) ->
          { spec: GenericExecuteSpec ->
            when (value) {
              is NullValue<*> -> spec.bindNull(key, value.type.java)
              else -> spec.bind(key, value)
            }
          }
        }
        .fold(executeSpecBinderMonoid)
