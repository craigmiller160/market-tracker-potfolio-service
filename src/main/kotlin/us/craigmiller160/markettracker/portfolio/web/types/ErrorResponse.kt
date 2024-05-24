package io.craigmiller160.markettracker.portfolio.web.types

data class ErrorResponse(val message: String, val status: Int, val method: String, val uri: String)
