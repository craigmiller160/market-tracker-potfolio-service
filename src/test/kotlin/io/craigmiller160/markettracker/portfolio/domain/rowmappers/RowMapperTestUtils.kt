package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import java.lang.IllegalArgumentException

fun typeLeft(column: String, type: Class<*>): Either<Throwable, SharesOwned> =
    Either.Left(
        IllegalArgumentException(
            "Error getting column $column", ClassCastException("Invalid type: ${type.name}")))

fun nullLeft(column: String): Either<Throwable, SharesOwned> =
    Either.Left(NullPointerException("Value is null: Missing $column column"))
