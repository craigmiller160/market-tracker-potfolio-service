package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MAX
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import java.math.BigDecimal
import java.time.LocalDate

class SharesOwnedBuilder {
  lateinit var start: LocalDate
  var end: LocalDate = DATE_RANGE_MAX
  lateinit var symbol: String
  lateinit var amount: BigDecimal
}

class TestDataBuilder {
  private val data = mutableListOf<SharesOwnedBuilder>()
  fun shares(init: SharesOwnedBuilder.() -> Unit) {
    data += SharesOwnedBuilder().also(init)
  }

  fun build(): List<SharesOwned> =
      data.map { builder ->
        SharesOwned(
            id = TypedId(),
            portfolioId = TypedId(),
            userId = TypedId(),
            dateRangeStart = builder.start,
            dateRangeEnd = builder.end,
            totalShares = builder.amount,
            symbol = builder.symbol)
      }
}

fun createTestData(init: TestDataBuilder.() -> Unit): List<SharesOwned> =
    TestDataBuilder().also(init).let { it.build() }
