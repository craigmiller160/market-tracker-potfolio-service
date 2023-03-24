package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class DatabaseClientPortfolioRepository(private val databaseClient: DatabaseClient) :
    PortfolioRepository {
  override suspend fun createPortfolio(portfolio: Portfolio): KtResult<Portfolio> {
    TODO("Not yet implemented")
  }
}
