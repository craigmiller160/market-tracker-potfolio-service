package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioNameResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
import org.springframework.stereotype.Service

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository,
    private val authorizationService: AuthorizationService
) {
  suspend fun getPortfolios(): TryEither<List<PortfolioResponse>> =
      portfolioRepository.findAllForUser(authorizationService.getUserId()).map { portfolios ->
        portfolios.map { it.toPortfolioNameResponse() }
      }
}
