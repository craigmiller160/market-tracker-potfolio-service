package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import kotlin.reflect.KClass
import kotlin.reflect.cast

class Row(private val data: Map<String, Any?>) {
  operator fun get(key: String): Any? = data[key]
  operator fun <T : Any> get(key: String, type: KClass<T>): TryEither<T>? =
      data[key]?.let { Either.catch { type.cast(it) } }
}
