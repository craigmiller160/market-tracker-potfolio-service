package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap

typealias TryEither<T> = Either<Throwable, T>

suspend fun <A, B, C> Either<A, B>.coFlatMap(block: suspend (B) -> Either<A, C>): Either<A, C> =
    flatMap {
      block(it)
    }
