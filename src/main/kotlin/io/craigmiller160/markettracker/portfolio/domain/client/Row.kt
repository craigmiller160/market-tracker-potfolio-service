package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.cast

data class Row(private val data: Map<String, Any?>) {
  fun <T : Any> getOptional(key: String, type: KClass<T>): TryEither<T?> =
      data[key]?.let {
        Either.catch { type.cast(it) }
            .mapLeft { IllegalArgumentException("Error casting column $key", it) }
      }
          ?: Either.Right(null)
  fun <T : Any> getRequired(key: String, type: KClass<T>): TryEither<T> =
      getOptional(key, type).flatMap { value -> value.leftIfNull("Missing $key column") }
}
