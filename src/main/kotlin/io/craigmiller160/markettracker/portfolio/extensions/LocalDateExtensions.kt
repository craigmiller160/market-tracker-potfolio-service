package io.craigmiller160.markettracker.portfolio.extensions

import java.time.LocalDate

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    (this.isAfter(startDate) || this.isEqual(startDate)) &&
        (this.isBefore(endDate) || this.isEqual(endDate))

fun LocalDate.isAfterOrEqual(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)

fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean =
    this.isBefore(other) || this.isEqual(other)
