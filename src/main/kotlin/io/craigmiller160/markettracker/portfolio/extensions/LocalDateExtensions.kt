package io.craigmiller160.markettracker.portfolio.extensions

import java.time.LocalDate

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    this.isAfter(startDate) && this.isBefore(endDate)
