package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierHistory
import java.math.BigDecimal
import java.time.LocalDate

private val objectMapper = jacksonObjectMapper()

private val data401k =
    DataLoader.load("data/craigmiller/Data401k.json").let {
      objectMapper.readValue(it, GoogleSpreadsheetValues::class.java)
    }
private val vtiHistory =
    DataLoader.load("data/craigmiller/TradierHistoryFor401k_VTI.json").let {
      objectMapper.readValue(it, TradierHistory::class.java)
    }
private val vxusHistory =
    DataLoader.load("data/craigmiller/TradierHistoryFor401k_VXUS.json").let {
      objectMapper.readValue(it, TradierHistory::class.java)
    }

val TEST_DATA_401K: List<SharesOwned> = createTestData {
  shares {
    start = LocalDate.of(2022, 2, 1)
    end = LocalDate.of(2022, 3, 1)
    symbol = "VTI"
    amount = BigDecimal("371.7")
  }
  shares {
    start = LocalDate.of(2022, 3, 1)
    end = LocalDate.of(2022, 4, 1)
    symbol = "VTI"
    amount = BigDecimal("403.8")
  }

  shares {
    start = LocalDate.of(2022, 4, 1)
    end = LocalDate.of(2022, 5, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 5, 1)
    end = LocalDate.of(2022, 6, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 7, 1)
    end = LocalDate.of(2022, 8, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 8, 1)
    end = LocalDate.of(2022, 9, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 9, 1)
    end = LocalDate.of(2022, 10, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 10, 1)
    end = LocalDate.of(2022, 11, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2022, 11, 1)
    end = LocalDate.of(2022, 12, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2023, 1, 1)
    end = LocalDate.of(2023, 2, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2023, 2, 1)
    end = LocalDate.of(2023, 3, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2023, 3, 1)
    end = LocalDate.of(2023, 4, 1)
    symbol = "VTI"
    amount = TODO()
  }

  shares {
    start = LocalDate.of(2023, 4, 1)
    end = LocalDate.of(2023, 5, 1)
    symbol = "VTI"
    amount = TODO()
  }
}
