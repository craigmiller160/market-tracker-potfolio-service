package io.craigmiller160.markettracker.portfolio.web.types

import java.math.BigDecimal
import java.time.LocalDateTime

// TODO rename the property and make ZDT probably... Actually look at what the UI requires
data class SharesOwnedResponse(val date: LocalDateTime, val shares: BigDecimal)
