package io.craigmiller160.markettracker.portfolio.web.types.tradier

data class TradierDay(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)

data class TradierInnerHistory(val day: List<TradierDay>)

data class TradierHistory(val history: TradierInnerHistory?)
