package io.craigmiller160.markettracker.portfolio.service

import arrow.core.sequence
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
    portfolioRepository.findAllForUser(userId).map { portfolios ->
      portfolios
          .map {
            CoroutineScope(Dispatchers.IO).async {
              sharesOwnedRepository.findUniqueStocksInPortfolio(userId, it.id)
            }
          }
          .awaitAll()
          .sequence()
      // TODO need to handle output
    }
  }
}
