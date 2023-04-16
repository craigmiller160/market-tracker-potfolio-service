package io.craigmiller160.markettracker.portfolio.extensions

import io.r2dbc.spi.Statement

private typealias StatementBatchBinder = (Statement) -> Statement

// TODO delete this whole file
fun <T> List<T>.toSqlBatches(statement: Statement, block: (T, Statement) -> Statement): Statement =
    mapToStatementBatch(block).reduceStatementBatches(statement)

private fun <T> List<T>.mapToStatementBatch(
    block: (T, Statement) -> Statement
): List<StatementBatchBinder> = map { record -> { stmt: Statement -> block(record, stmt) } }

private fun List<StatementBatchBinder>.reduceStatementBatches(statement: Statement): Statement {
  if (isEmpty()) {
    return statement
  }

  return reduce { first, second ->
    { stmt ->
      first(stmt).add()
      second(stmt)
    }
  }(statement)
}
