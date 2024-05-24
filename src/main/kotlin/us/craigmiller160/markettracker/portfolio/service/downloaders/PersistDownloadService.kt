package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.github.craigmiller160.fpresultkt.transaction.extensions.executeAndAwaitEither
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator

@Service
class PersistDownloadService(
    private val portfolioRepository: PortfolioRepository,
    private val sharesOwnedRepository: SharesOwnedRepository,
    private val transactionOperator: TransactionalOperator
) {
  private val log = LoggerFactory.getLogger(javaClass)
  suspend fun persistPortfolios(
      portfolios: List<PortfolioWithHistory>
  ): TryEither<List<PortfolioWithHistory>> {
    log.info("Persisting portfolio data")
    return transactionOperator.executeAndAwaitEither {
      deletePortfolios(portfolios).flatMap { portfolios.map { createPortfolio(it) }.bindToList() }
    }
  }

  private suspend fun deletePortfolios(portfolios: List<Portfolio>): TryEither<Unit> {
    val userIds = portfolios.asSequence().map { it.userId }.distinct().toList()
    log.debug("Deleting all data for users ${userIds}")
    return sharesOwnedRepository.deleteAllSharesOwnedForUsers(userIds).flatMap {
      portfolioRepository.deleteAllPortfoliosForUsers(userIds)
    }
  }

  private suspend fun createPortfolio(
      portfolio: PortfolioWithHistory
  ): TryEither<PortfolioWithHistory> {
    log.debug("Writing data for portfolio ${portfolio.id} for user ${portfolio.userId}")
    return portfolioRepository
        .createPortfolio(portfolio)
        .flatMap { sharesOwnedRepository.createAllSharesOwned(portfolio.ownershipHistory) }
        .map { portfolio }
  }
}
