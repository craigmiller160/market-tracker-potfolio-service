package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.Either
import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository

@Repository
class DatabaseClientPortfolioRepository(
    private val databaseClient: DatabaseClient,
    private val sqlLoader: SqlLoader
) : PortfolioRepository {
  companion object {
    private const val INSERT_PORTFOLIO_SQL = "portfolio/insertPortfolio.sql"
  }

  override suspend fun createPortfolio(portfolio: Portfolio): TryEither<Portfolio> =
      sqlLoader
          .loadSql(INSERT_PORTFOLIO_SQL)
          .flatMap { sql ->
            Either.catch {
              databaseClient
                  .sql(sql)
                  .bind("id", portfolio.id.value)
                  .bind("userId", portfolio.userId.value)
                  .bind("name", portfolio.name)
                  .fetch()
                  .awaitRowsUpdated()
            }
          }
          .map { portfolio }
}
