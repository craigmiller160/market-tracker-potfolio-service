package io.craigmiller160.markettracker.portfolio.web.types

import java.math.BigDecimal
import java.time.ZonedDateTime

data class SharesOwnedResponse(val date: ZonedDateTime, val shares: BigDecimal)
