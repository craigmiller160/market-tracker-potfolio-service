package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import java.lang.NullPointerException
import java.util.UUID
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class RowTest {
  companion object {
    private const val KEY = "hello"
    private val VALUE = UUID.randomUUID()
    private val VALUE_TYPE = UUID::class

    @JvmStatic
    fun getOptionalValues(): Stream<RowValues> =
        Stream.of(
            RowValues(KEY, VALUE, VALUE_TYPE, Either.Right(VALUE)),
            RowValues(KEY, VALUE, Int::class, classCastLeft(Int::class)),
            RowValues(KEY, null, Int::class, Either.Right(null)))

    @JvmStatic
    fun getRequiredValues(): Stream<RowValues> =
        Stream.of(
            RowValues(KEY, VALUE, VALUE_TYPE, Either.Right(VALUE)),
            RowValues(KEY, VALUE, Int::class, classCastLeft(Int::class)),
            RowValues(KEY, null, Int::class, nullLeft(KEY)))
  }

  @ParameterizedTest
  @MethodSource("getOptionalValues")
  fun `get optional`(values: RowValues) {
    val row = Row(mapOf(values.key to values.value))
    val result = row.getOptional(values.key, values.valueType)
    when (values.expected) {
      is Either.Right<*> -> result.shouldBeRight(values.expected.value)
      is Either.Left<*> -> result.shouldBeLeft(values.expected.value)
    }
  }

  @ParameterizedTest
  @MethodSource("getRequiredValues")
  fun `get required`(values: RowValues) {
    val row = Row(mapOf(values.key to values.value))
    val result = row.getRequired(values.key, values.valueType)
    when (values.expected) {
      is Either.Right<*> -> result.shouldBeRight(values.expected.value)
      is Either.Left<*> -> result.shouldBeLeft(values.expected.value)
    }
  }
}

data class RowValues(
    val key: String,
    val value: Any?,
    val valueType: KClass<*>,
    val expected: TryEither<*>
)

private fun classCastLeft(type: KClass<*>): TryEither<*> =
    Either.Left(ClassCastException("Value cannot be cast to ${type.qualifiedName}"))

private fun nullLeft(name: String): TryEither<*> =
    Either.Left(NullPointerException("Value is null: Missing $name column"))
