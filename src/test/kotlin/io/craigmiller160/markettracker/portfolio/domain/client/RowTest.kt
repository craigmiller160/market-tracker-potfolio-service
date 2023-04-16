package io.craigmiller160.markettracker.portfolio.domain.client

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import java.util.UUID
import org.junit.jupiter.api.Test

class RowTest {
  companion object {
    private const val KEY = "hello"
    private val VALUE = UUID.randomUUID()
    private val VALUE_TYPE = UUID::class
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
