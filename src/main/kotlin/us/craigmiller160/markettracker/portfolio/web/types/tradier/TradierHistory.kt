package io.craigmiller160.markettracker.portfolio.web.types.tradier

import java.time.LocalDate

data class TradierDay(
    val date: LocalDate,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)

data class TradierInnerHistory(val day: List<TradierDay>)

data class TradierHistory(val history: TradierInnerHistory?)
