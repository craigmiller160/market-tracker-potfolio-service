package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import arrow.core.sequence
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class PersistDownloadService(
    private val portfolioRepository: PortfolioRepository,
    private val sharesOwnedRepository: SharesOwnedRepository,
    private val transactionOperator: TransactionalOperator
) {
  // TODO delete all @Transactional uses
  suspend fun persistPortfolios(
      portfolios: List<PortfolioWithHistory>
  ): TryEither<List<PortfolioWithHistory>> =
      transactionOperator.executeAndAwait { tx ->
        portfolios
            .map { createPortfolio(it) }
            .sequence()
            .onLeft { tx.setRollbackOnly() } // TODO create extension function to automate this
      }

  private suspend fun createPortfolio(
      portfolio: PortfolioWithHistory
  ): TryEither<PortfolioWithHistory> =
      portfolioRepository
          .createPortfolio(portfolio)
          .flatMap { sharesOwnedRepository.createAllSharesOwned(portfolio.ownershipHistory) }
          .map { portfolio }
}
