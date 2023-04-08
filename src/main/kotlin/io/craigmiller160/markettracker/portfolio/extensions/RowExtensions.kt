package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap
import io.r2dbc.spi.Row

fun <T> Row.getRequired(name: String, type: Class<T>): TryEither<T> =
    Either.catch { get(name, type) }
        .mapLeft { ex -> IllegalArgumentException("Error getting column $name", ex) }
        .flatMap { it.leftIfNull("Missing $name column") }

fun <T> Row.getOptional(name: String, type: Class<T>): TryEither<T?> =
    Either.catch { get(name, type) }
        .fold({ ex ->
          when (ex) {
            is NoSuchElementException -> Either.Right(null)
            else -> Either.Left(ex)
          }
        }) {
          Either.Right(it)
        }
