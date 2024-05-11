package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import java.util.Optional

typealias TryEither<T> = Either<Throwable, T>

suspend fun <A, B, C> Either<A, B>.coFlatMap(block: suspend (B) -> Either<A, C>): Either<A, C> =
    flatMap {
      block(it)
    }

fun <T> T?.leftIfNull(message: String = ""): TryEither<T> =
    this?.let { Either.Right(it) } ?: Either.Left(NullPointerException("Value is null: $message"))

@Suppress("USELESS_CAST")
fun <T> Optional<T>.leftIfEmpty(message: String = ""): TryEither<T> =
    this.map { Either.Right(it) as Either<Throwable, T> }
        .orElse(Either.Left(NullPointerException("Value is null: $message")))

inline fun <B, C> TryEither<B>.mapCatch(block: (B) -> C): TryEither<C> = flatMap { value ->
  Either.catch { block(value) }
}

fun <E, A> List<Either<E, A>>.bindToList(): Either<E, List<A>> = either {
  this@bindToList.bindAll()
}
