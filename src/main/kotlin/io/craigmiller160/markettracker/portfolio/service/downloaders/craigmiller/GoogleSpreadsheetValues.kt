package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

enum class MajorDimension {
  ROWS,
  COLUMNS
}

data class GoogleSpreadsheetValues(
    val range: String,
    val majorDimension: MajorDimension,
    val rows: List<List<String>>
)
