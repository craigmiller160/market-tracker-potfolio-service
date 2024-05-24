package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import java.math.BigDecimal
import java.time.LocalDate

val TEST_DATA_STANDARD: List<SharesOwned> = createTestData {
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
    end = LocalDate.of(2020, 6, 22)
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
    end = LocalDate.of(2020, 8, 19)
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
