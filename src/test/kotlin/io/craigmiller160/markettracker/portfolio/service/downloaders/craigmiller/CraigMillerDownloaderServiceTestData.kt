package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import java.math.BigDecimal
import java.time.LocalDate

val TEST_DATA: List<SharesOwned> = createTestData {
  shares {
    start = LocalDate.of(2020, 6, 4)
    end = LocalDate.of(2020, 6, 19)
    symbol = "COTY"
    amount = BigDecimal("1")
  }
  shares {
    start = LocalDate.of(2020, 6, 19)
    symbol = "COTY"
    amount = BigDecimal("0")
  }
  shares {
    start = LocalDate.of(2020, 6, 15)
    symbol = "UAL"
    amount = BigDecimal("0")
  }
  shares {
    start = LocalDate.of(2020, 6, 10)
    end = LocalDate.of(2020, 6, 15)
    symbol = "VYM"
    amount = BigDecimal("1")
  }
  shares {
    start = LocalDate.of(2020, 6, 15)
    end = LocalDate.of(2020, 6, 21)
    symbol = "VYM"
    amount = BigDecimal("2")
  }
  shares {
    start = LocalDate.of(2020, 6, 22)
    end = LocalDate.of(2020, 10, 18)
    symbol = "VYM"
    amount = BigDecimal("4")
  }
  shares {
    start = LocalDate.of(2020, 10, 18)
    symbol = "VYM"
    amount = BigDecimal("6")
  }
  shares {
    start = LocalDate.of(2020, 7, 7)
    end = LocalDate.of(2020, 7, 8)
    symbol = "SPYG"
    amount = BigDecimal("2")
  }
  shares {
    start = LocalDate.of(2020, 7, 8)
    end = LocalDate.of(2020, 8, 18)
    symbol = "SPYG"
    amount = BigDecimal("4")
  }
  shares {
    start = LocalDate.of(2020, 8, 19)
    symbol = "SPYG"
    amount = BigDecimal("5")
  }
  shares {
    start = LocalDate.of(2020, 7, 8)
    end = LocalDate.of(2020, 10, 18)
    symbol = "VOOV"
    amount = BigDecimal("1")
  }
  shares {
    start = LocalDate.of(2020, 10, 18)
    symbol = "VOOV"
    amount = BigDecimal("2")
  }
  shares {
    start = LocalDate.of(2020, 7, 8)
    end = LocalDate.of(2020, 10, 18)
    symbol = "VTI"
    amount = BigDecimal("1")
  }
  shares {
    start = LocalDate.of(2020, 10, 18)
    symbol = "VTI"
    amount = BigDecimal("2")
  }
  shares {
    start = LocalDate.of(2020, 7, 8)
    symbol = "VB"
    amount = BigDecimal("1")
  }
}

private class SharesOwnedBuilder {
  lateinit var start: LocalDate
  var end: LocalDate = DownloaderService.MAX_DATE
  lateinit var symbol: String
  lateinit var amount: BigDecimal
}

private class TestDataBuilder {
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

private fun createTestData(init: TestDataBuilder.() -> Unit): List<SharesOwned> =
    TestDataBuilder().also(init).let { it.build() }
