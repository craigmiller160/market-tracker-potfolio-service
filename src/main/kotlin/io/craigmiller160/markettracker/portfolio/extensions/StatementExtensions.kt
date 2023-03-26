package io.craigmiller160.markettracker.portfolio.extensions

import io.r2dbc.spi.Statement

typealias StatementBatchBinder = (Statement) -> Statement

fun <T> List<T>.mapToStatementBatch(
    block: (T, Statement) -> Statement
): List<StatementBatchBinder> = map { record -> { stmt: Statement -> block(record, stmt) } }

fun List<StatementBatchBinder>.reduceStatementBatches(statement: Statement): Statement =
    reduce { first, second ->
      { stmt ->
        first(stmt).add()
        second(stmt)
      }
    }(statement)
