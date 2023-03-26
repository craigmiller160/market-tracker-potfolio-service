package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either

typealias TryEither<T> = Either<Throwable, T>
