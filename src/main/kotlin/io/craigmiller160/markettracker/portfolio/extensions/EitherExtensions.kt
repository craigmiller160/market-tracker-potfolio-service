package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap

typealias TryEither<T> = Either<Throwable, T>

suspend fun <A, B, C> Either<A, B>.coFlatMap(block: suspend (B) -> Either<A, C>): Either<A, C> =
    flatMap {
      block(it)
    }

fun <T> T?.leftIfNull(message: String): TryEither<T> =
    Either.Left(NullPointerException("Value is null: $message"))

fun <B, C> TryEither<B>.mapCatch(block: (B) -> C): TryEither<C> = flatMap { value ->
  Either.catch { block(value) }
}
