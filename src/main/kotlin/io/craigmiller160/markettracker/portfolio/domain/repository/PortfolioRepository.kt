package io.craigmiller160.markettracker.portfolio.domain.repository

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.TryEither

interface PortfolioRepository {
  suspend fun createPortfolio(portfolio: Portfolio): TryEither<Portfolio>

  suspend fun createAllPortfolios(portfolios: List<Portfolio>): TryEither<List<Portfolio>>

  suspend fun findAllForUser(userId: TypedId<UserId>): TryEither<List<Portfolio>>
}
