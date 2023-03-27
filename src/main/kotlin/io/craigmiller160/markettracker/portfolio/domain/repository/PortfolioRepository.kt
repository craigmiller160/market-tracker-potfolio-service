package io.craigmiller160.markettracker.portfolio.domain.repository

import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.TryEither

interface PortfolioRepository {
  suspend fun createPortfolio(portfolio: Portfolio): TryEither<Portfolio>
}
