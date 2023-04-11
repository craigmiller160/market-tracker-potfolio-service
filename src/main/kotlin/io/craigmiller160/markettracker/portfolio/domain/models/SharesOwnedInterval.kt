package io.craigmiller160.markettracker.portfolio.domain.models

enum class SharesOwnedInterval(val sql: String) {
  SINGLE("1 day"),
  DAILY("1 day"),
  WEEKLY("1 week"),
  MONTHLY("1 month")
}
