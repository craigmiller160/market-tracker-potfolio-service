package io.craigmiller160.markettracker.portfolio.web.handlers

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.getOrElse
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.extensions.leftIfEmpty
import io.craigmiller160.markettracker.portfolio.service.PortfolioService
import io.craigmiller160.markettracker.portfolio.service.SharesOwnedService
import io.craigmiller160.markettracker.portfolio.web.exceptions.BadRequestException
import io.craigmiller160.markettracker.portfolio.web.exceptions.MissingParameterException
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
    TODO()
  }

  suspend fun getStocksForAllPortfoliosCombined(request: ServerRequest): ServerResponse =
      sharesOwnedService.findUniqueStocksForAllPortfoliosCombined().toResponse()

  suspend fun getSharesOwnedForAllPortfoliosCombinedStock(request: ServerRequest): ServerResponse {
    val stockSymbol = request.stockSymbol
    val startDate = request.startDate
    val endDate = request.endDate
    val interval = request.interval
    TODO()
  }

  private val ServerRequest.portfolioId: TypedId<PortfolioId>
    get() =
        Either.catch { pathVariable("portfolioId").let { TypedId<PortfolioId>(it) } }
            .getOrElse { throw BadRequestException("Error parsing portfolioId", it) }

  private val ServerRequest.stockSymbol: String
    get() =
        Either.catch { pathVariable("stockSymbol") }
            .getOrElse { throw BadRequestException("Error parsing stockSymbol", it) }

  private val ServerRequest.startDate: LocalDate
    get() =
        either
            .eager {
              val startDateString =
                  queryParam("startDate")
                      .leftIfEmpty()
                      .mapLeft { MissingParameterException("startDate") }
                      .bind()
              Either.catch { LocalDate.parse(startDateString) }
                  .mapLeft { BadRequestException("Error parsing startDate", it) }
                  .bind()
            }
            .getOrElse { throw it }

  private val ServerRequest.endDate: LocalDate
    get() =
        queryParam("endDate")
            .map { LocalDate.parse(it) }
            .orElseThrow { MissingParameterException("endDate") }

  private val ServerRequest.interval: SharesOwnedInterval
    get() =
        queryParam("interval")
            .map { SharesOwnedInterval.valueOf(it) }
            .orElseThrow { MissingParameterException("interval") }
}
