package io.craigmiller160.markettracker.portfolio.extensions

import java.time.LocalDate

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    this.isAfterOrEqual(startDate) && this.isBeforeOrEqual(endDate)

fun LocalDate.isAfterOrEqual(other: LocalDate): Boolean = this.isAfter(other) || this.isEqual(other)

fun LocalDate.isBeforeOrEqual(other: LocalDate): Boolean =
    this.isBefore(other) || this.isEqual(other)
