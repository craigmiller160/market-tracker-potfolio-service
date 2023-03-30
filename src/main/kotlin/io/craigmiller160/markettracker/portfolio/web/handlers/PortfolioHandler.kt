package io.craigmiller160.markettracker.portfolio.web.handlers

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PortfolioHandler {
  suspend fun getPortfolioNames(request: ServerRequest): ServerResponse = TODO()
}
