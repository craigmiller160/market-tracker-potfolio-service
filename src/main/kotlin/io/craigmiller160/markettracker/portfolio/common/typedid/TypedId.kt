package io.craigmiller160.markettracker.portfolio.common.typedid

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@JvmInline
@Schema(implementation = UUID::class)
value class TypedId<T>(val value: UUID = UUID.randomUUID()) {
  constructor(value: String) : this(UUID.fromString(value))
  override fun toString(): String = value.toString()
}

fun <T> UUID.toTypedId(): TypedId<T> = TypedId(this)

fun <T> String.toTypedId(): TypedId<T> = TypedId(UUID.fromString(this))
