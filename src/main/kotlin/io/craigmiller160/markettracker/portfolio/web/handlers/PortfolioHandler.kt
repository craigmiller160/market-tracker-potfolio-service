package io.craigmiller160.markettracker.portfolio.web.handlers

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.toTypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
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
  suspend fun getPortfolios(request: ServerRequest): ServerResponse =
      portfolioService.getPortfolios().toResponse()

  suspend fun getStocksForPortfolio(request: ServerRequest): ServerResponse =
      sharesOwnedService.findUniqueStocksInPortfolio(request.portfolioId).toResponse()

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

  suspend fun getStocksForAllPortfoliosCombined(request: ServerRequest): ServerResponse =
      sharesOwnedService.findUniqueStocksForAllPortfoliosCombined().toResponse()

  suspend fun getSharesOwnedForAllPortfoliosCombinedStock(request: ServerRequest): ServerResponse {
    val stockSymbol = request.stockSymbol
    val startDate = request.startDate
    val endDate = request.endDate
    val interval = request.interval
    return sharesOwnedService
        .getSharesOwnedForUserStock(stockSymbol, startDate, endDate, interval)
        .toResponse()
  }

  suspend fun getCurrentValueForStockInPortfolio(request: ServerRequest): ServerResponse {
    TODO()
  }

  suspend fun getCurrentValueForStockForUser(request: ServerRequest): ServerResponse {
    TODO()
  }

  private val ServerRequest.portfolioId: TypedId<PortfolioId>
    get() = pathVariable("portfolioId", String::toTypedId)

  private val ServerRequest.stockSymbol: String
    get() = pathVariable("stockSymbol") { it }

  private val ServerRequest.startDate: LocalDate
    get() = requiredQueryParam("startDate", LocalDate::parse)

  private val ServerRequest.endDate: LocalDate
    get() = requiredQueryParam("endDate", LocalDate::parse)

  private val ServerRequest.interval: SharesOwnedInterval
    get() = requiredQueryParam("interval", SharesOwnedInterval::valueOf)
}
