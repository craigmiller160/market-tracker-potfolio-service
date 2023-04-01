package io.craigmiller160.markettracker.portfolio.web.handlers

import io.craigmiller160.markettracker.portfolio.service.PortfolioService
import io.craigmiller160.markettracker.portfolio.web.response.toResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PortfolioHandler(private val portfolioService: PortfolioService) {
  suspend fun getPortfolios(request: ServerRequest): ServerResponse =
      portfolioService.getPortfolios().toResponse()
}
