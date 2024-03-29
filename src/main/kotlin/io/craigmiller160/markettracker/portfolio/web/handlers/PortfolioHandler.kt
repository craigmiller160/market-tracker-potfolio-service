package io.craigmiller160.markettracker.portfolio.web.handlers

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.toTypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.extensions.optionalQueryParam
import io.craigmiller160.markettracker.portfolio.extensions.pathVariable
import io.craigmiller160.markettracker.portfolio.extensions.requiredQueryParam
import io.craigmiller160.markettracker.portfolio.service.PortfolioService
import io.craigmiller160.markettracker.portfolio.service.SharesOwnedService
import io.craigmiller160.markettracker.portfolio.web.response.toResponse
import java.time.LocalDate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PortfolioHandler(
    private val portfolioService: PortfolioService,
    private val sharesOwnedService: SharesOwnedService
) {
  suspend fun getPortfolios(request: ServerRequest): ServerResponse {
    val startDate = request.optionalStartDate
    val endDate = request.optionalEndDate
    return portfolioService.getPortfolios(startDate, endDate).toResponse()
  }

  suspend fun getSharesOwnedForPortfolioStock(request: ServerRequest): ServerResponse {
    val portfolioId = request.portfolioId
    val stockSymbol = request.stockSymbol
    val startDate = request.startDate
    val endDate = request.endDate
    val interval = request.interval
    return sharesOwnedService
        .getSharesOwnedForPortfolioStock(portfolioId, stockSymbol, startDate, endDate, interval)
        .toResponse()
  }

  suspend fun getCurrentValueForStockInPortfolio(request: ServerRequest): ServerResponse {
    val portfolioId = request.portfolioId
    val stockSymbol = request.stockSymbol
    return sharesOwnedService
        .getCurrentSharesOwnedForPortfolioStock(portfolioId, stockSymbol)
        .toResponse()
  }

  private val ServerRequest.portfolioId: TypedId<PortfolioId>
    get() = pathVariable("portfolioId", String::toTypedId)

  private val ServerRequest.stockSymbol: String
    get() = pathVariable("stockSymbol") { it }

  private val ServerRequest.optionalStartDate: LocalDate?
    get() = optionalQueryParam("startDate", LocalDate::parse)
  private val ServerRequest.optionalEndDate: LocalDate?
    get() = optionalQueryParam("endDate", LocalDate::parse)

  private val ServerRequest.startDate: LocalDate
    get() = requiredQueryParam("startDate", LocalDate::parse)

  private val ServerRequest.endDate: LocalDate
    get() = requiredQueryParam("endDate", LocalDate::parse)

  private val ServerRequest.interval: SharesOwnedInterval
    get() = requiredQueryParam("interval", SharesOwnedInterval::valueOf)
}
