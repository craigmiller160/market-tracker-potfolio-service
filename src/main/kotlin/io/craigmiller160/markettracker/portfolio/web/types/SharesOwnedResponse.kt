package io.craigmiller160.markettracker.portfolio.web.types

import java.math.BigDecimal
import java.time.LocalDate

data class SharesOwnedResponse(val date: LocalDate, val symbol: String, val shares: BigDecimal)
