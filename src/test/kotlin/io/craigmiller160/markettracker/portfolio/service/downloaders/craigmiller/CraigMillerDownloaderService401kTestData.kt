package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierHistory
import java.math.BigDecimal
import java.time.LocalDate

// 12/2022 is when VXUS starts

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
    amount = BigDecimal("228.0236")
  }
}
