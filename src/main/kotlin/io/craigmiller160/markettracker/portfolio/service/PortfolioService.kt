package io.craigmiller160.markettracker.portfolio.service

import arrow.core.flatMap
import arrow.core.sequence
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
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
  suspend fun getPortfolios(): TryEither<List<PortfolioResponse>> {
    val userId = authorizationService.getUserId()
    return portfolioRepository.findAllForUser(userId).flatMap { portfolios ->
      portfolios
          .map { portfolio ->
            CoroutineScope(Dispatchers.IO).async {
              sharesOwnedRepository.findUniqueStocksInPortfolio(userId, portfolio.id).map { stocks
                ->
                portfolio.toPortfolioResponse(stocks)
              }
            }
          }
          .awaitAll()
          .sequence()
    }
  }
}
