package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import arrow.core.sequence
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.github.craigmiller160.fpresultkt.transaction.extensions.executeAndAwaitEither
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator

@Service
class PersistDownloadService(
    private val portfolioRepository: PortfolioRepository,
    private val sharesOwnedRepository: SharesOwnedRepository,
    private val transactionOperator: TransactionalOperator
) {
  suspend fun persistPortfolios(
      portfolios: List<PortfolioWithHistory>
  ): TryEither<List<PortfolioWithHistory>> =
      transactionOperator.executeAndAwaitEither {
        portfolios.map { createPortfolio(it) }.sequence()
      }

  private suspend fun createPortfolio(
      portfolio: PortfolioWithHistory
  ): TryEither<PortfolioWithHistory> =
      portfolioRepository
          .createPortfolio(portfolio)
          .flatMap { sharesOwnedRepository.createAllSharesOwned(portfolio.ownershipHistory) }
          .map { portfolio }
}
