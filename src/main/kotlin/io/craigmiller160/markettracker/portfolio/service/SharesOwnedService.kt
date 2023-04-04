package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import org.springframework.stereotype.Service

@Service
class SharesOwnedService(private val sharesOwnedRepository: SharesOwnedRepository) {
  suspend fun findUniqueStocksInPortfolio(
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>> = sharesOwnedRepository.findUniqueStocksInPortfolio(portfolioId)
}
