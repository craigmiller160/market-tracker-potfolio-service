package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersistDownloadService(private val portfolioRepository: PortfolioRepository) {
  @Transactional
  fun persistPortfolios(portfolios: List<PortfolioWithHistory>): KtResult<Unit> {
    TODO()
  }
}
