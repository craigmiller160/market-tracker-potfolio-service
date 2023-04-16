package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.domain.client.Row
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

typealias RowMapperData<T> = Pair<Map<String, Any?>, TryEither<T>>

fun <T> typeLeft(column: String, type: KClass<*>): Either<Throwable, T> =
    Either.Left(
        IllegalArgumentException(
            "Error getting column $column",
            ClassCastException("Value cannot be cast to ${type.qualifiedName}")))

fun <T> nullLeft(column: String): Either<Throwable, T> =
    Either.Left(NullPointerException("Value is null: Missing $column column"))

fun <T> validateRowMapper(rowMapper: RowMapper<T>, data: RowMapperData<T>) {
  val (map, expected) = data
  val row = Row(map)
  val actual = rowMapper(row)
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
