package io.craigmiller160.markettracker.portfolio.common.typedid

import java.util.UUID

data class TypedId<T>(val value: UUID = UUID.randomUUID()) : Comparable<TypedId<T>> {
  constructor() : this(UUID.randomUUID())
  constructor(value: String) : this(UUID.fromString(value))

  override fun compareTo(other: TypedId<T>): Int = value.compareTo(other.value)
}
