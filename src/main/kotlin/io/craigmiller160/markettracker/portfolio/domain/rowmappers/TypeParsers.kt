package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import com.github.michaelbull.result.Err
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias TypeParser<T, R> = (T) -> KtResult<R>

private val DATE_RANGE_REGEX = Regex("^\\[(?<start>.+),(?<end>.+)\\)")

val dateRangeTypeParser: TypeParser<String, Pair<LocalDate, LocalDate>> = { dateRange ->
  DATE_RANGE_REGEX.find(dateRange)?.let { result ->
    ktRunCatching {
      val start =
          result.groups["start"]!!.value.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
      val end = result.groups["end"]!!.value.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
      start to end
    }
  }
      ?: Err(IllegalArgumentException("String does not match daterange pattern"))
}
