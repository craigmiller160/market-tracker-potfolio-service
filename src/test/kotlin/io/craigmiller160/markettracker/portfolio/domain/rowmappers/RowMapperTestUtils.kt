package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import java.lang.IllegalArgumentException

fun <T> typeLeft(column: String, type: Class<*>): Either<Throwable, T> =
    Either.Left(
        IllegalArgumentException(
            "Error getting column $column", ClassCastException("Invalid type: ${type.name}")))

fun <T> nullLeft(column: String): Either<Throwable, T> =
    Either.Left(NullPointerException("Value is null: Missing $column column"))
