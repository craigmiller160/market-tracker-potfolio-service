package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap
import io.r2dbc.spi.Row

fun <T> Row.getRequired(name: String, type: Class<T>): TryEither<T> =
    Either.catch { get(name, type) }
        .mapLeft { ex -> IllegalArgumentException("Error getting column $name", ex) }
        .flatMap { it.leftIfNull("Missing $name column") }
