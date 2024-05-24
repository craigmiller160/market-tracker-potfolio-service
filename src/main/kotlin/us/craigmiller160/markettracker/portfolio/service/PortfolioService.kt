package io.craigmiller160.markettracker.portfolio.service

import arrow.core.flatMap
import arrow.core.raise.either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.springframework.stereotype.Service

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository,
    private val sharesOwnedRepository: SharesOwnedRepository,
    private val authorizationService: AuthorizationService
) {
  suspend fun getPortfolios(
      startDate: LocalDate?,
      endDate: LocalDate?
  ): TryEither<List<PortfolioResponse>> {
    val userId = authorizationService.getUserId()
    return either {
      val allPortfolios =
          portfolioRepository
              .findAllForUser(userId)
              .flatMap { getStocksAndBuildResponse(userId, it, startDate, endDate) }
              .bind()

      val combinedPortfolio = getCombinedPortfolio(userId, startDate, endDate).bind()
      allPortfolios + combinedPortfolio
    }
  }

  private suspend fun getCombinedPortfolio(
      userId: TypedId<UserId>,
      startDate: LocalDate?,
      endDate: LocalDate?
  ): TryEither<PortfolioResponse> =
      sharesOwnedRepository.findUniqueStocksForUser(userId, startDate, endDate).map { stocks ->
        PortfolioResponse(
            id = PortfolioConstants.COMBINED_PORTFOLIO_ID,
            name = PortfolioConstants.COMBINED_PORTFOLIO_NAME,
            stockSymbols = stocks)
      }

  private suspend fun getStocksAndBuildResponse(
      userId: TypedId<UserId>,
      portfolios: List<Portfolio>,
      startDate: LocalDate?,
      endDate: LocalDate?
  ): TryEither<List<PortfolioResponse>> =
      portfolios
          .map { portfolio ->
            CoroutineScope(Dispatchers.IO).async {
              sharesOwnedRepository
                  .findUniqueStocksInPortfolio(userId, portfolio.id, startDate, endDate)
                  .map { stocks -> portfolio.toPortfolioResponse(stocks) }
            }
          }
          .awaitAll()
          .bindToList()
}
