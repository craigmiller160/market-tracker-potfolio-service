package io.craigmiller160.markettracker.portfolio.common.typedid

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(implementation = UUID::class)
class TypedId<T>(val value: UUID = UUID.randomUUID()) : Comparable<TypedId<T>> {
  constructor() : this(UUID.randomUUID())
  constructor(value: String) : this(UUID.fromString(value))

  override fun compareTo(other: TypedId<T>): Int = value.compareTo(other.value)
  override fun equals(other: Any?): Boolean = other is TypedId<*> && value == other.value
  override fun hashCode(): Int = value.hashCode()
  override fun toString(): String = value.toString()
}
