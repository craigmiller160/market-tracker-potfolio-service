package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.r2dbc.spi.ColumnMetadata
import io.r2dbc.spi.RowMetadata
import java.lang.IllegalArgumentException

typealias RowMapperData<T> = Pair<Map<String, Any?>, TryEither<T>>

fun <T> typeLeft(column: String, type: Class<*>): Either<Throwable, T> =
    Either.Left(
        IllegalArgumentException(
            "Error getting column $column", ClassCastException("Invalid type: ${type.name}")))

fun <T> nullLeft(column: String): Either<Throwable, T> =
    Either.Left(NullPointerException("Value is null: Missing $column column"))

private val METADATA: RowMetadata =
    object : RowMetadata {
      override fun getColumnMetadata(index: Int): ColumnMetadata = TODO()
      override fun getColumnMetadata(name: String): ColumnMetadata = TODO()
      override fun getColumnMetadatas(): MutableList<out ColumnMetadata> = TODO()
    }

fun <T> validateRowMapper(rowMapper: RowMapper<T>, data: RowMapperData<T>) {
  val (map, expected) = data
  val row = MockRow(map)
  val actual = rowMapper(row, METADATA)
  when (expected) {
    is Either.Right -> actual.shouldBeRight(expected.value)
    is Either.Left -> {
      val actualException = actual.shouldBeLeft(expected.value)
      expected.value.cause?.let { expectedCause ->
        val actualCause = actualException.cause.shouldNotBeNull()
        actualCause.javaClass.shouldBe(expectedCause.javaClass)
        actualCause.message.shouldBe(expectedCause.message)
      }
    }
  }
}
