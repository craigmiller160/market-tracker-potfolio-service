package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import java.lang.NullPointerException
import java.util.UUID
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.junit.jupiter.api.Test

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
            RowValues(KEY, VALUE, Int::class, nullLeft(KEY)))
  }
  @Test
  fun `get optional, not null, with valid type`() {
    val row = Row(mapOf(KEY to VALUE))
    val result = row.getOptional(KEY, VALUE_TYPE)
    result.shouldBeRight(VALUE)
  }

  @Test
  fun `get optional, not null, with invalid type`() {
    val row = Row(mapOf(KEY to VALUE))
    val result = row.getOptional(KEY, Int::class)
    result.shouldBeLeft(ClassCastException("Value cannot be cast to ${Int::class.qualifiedName}"))
  }

  @Test
  fun `get optional, is null`() {
    val row = Row(mapOf(KEY to null))
    val result = row.getOptional(KEY, Int::class)
    result.shouldBeRight(null)
  }

  @Test
  fun `get required, not null, with valid type`() {
    val row = Row(mapOf(KEY to VALUE))
    val result = row.getRequired(KEY, VALUE_TYPE)
    result.shouldBeRight(VALUE)
  }

  @Test
  fun `get required, not null, with invalid type`() {
    TODO()
  }

  @Test
  fun `get required, is null`() {
    TODO()
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
