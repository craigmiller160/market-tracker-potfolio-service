package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import io.craigmiller160.markettracker.portfolio.extensions.mapCatch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias TypeParser<T, R> = (T) -> TryEither<R>

private val DATE_RANGE_REGEX = Regex("^\\[(?<start>.+),(?<end>.+)\\)")

val dateRangeTypeParser: TypeParser<String, Pair<LocalDate, LocalDate>> = { dateRange ->
  DATE_RANGE_REGEX.find(dateRange).leftIfNull("String does not match daterange pattern").mapCatch {
      result ->
    val start =
        result.groups["start"]!!.value.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
    val end = result.groups["end"]!!.value.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
    start to end
  }
}
