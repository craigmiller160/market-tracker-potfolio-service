package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.Either
import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.portfolioRowMapper
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.coFlatMap
import io.craigmiller160.markettracker.portfolio.extensions.mapCatch
import io.craigmiller160.markettracker.portfolio.extensions.toSqlBatches
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository
import reactor.kotlin.core.publisher.toFlux

@Repository
class DatabaseClientPortfolioRepository(
    private val databaseClient: DatabaseClient,
    private val sqlLoader: SqlLoader
) : PortfolioRepository {
  companion object {
    private const val INSERT_PORTFOLIO_SQL = "portfolio/insertPortfolio.sql"
    private const val INSERT_PORTFOLIO_BATCH_SQL = "portfolio/insertPortfolioBatch.sql"
    private const val FIND_ALL_FOR_USER_SQL = "portfolio/findAllForUser.sql"
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

  override suspend fun createPortfolios(portfolios: List<Portfolio>): TryEither<List<Portfolio>> =
      sqlLoader.loadSql(INSERT_PORTFOLIO_BATCH_SQL).coFlatMap(createAsBatch(portfolios)).map {
        portfolios
      }

  override suspend fun findAllForUser(userId: TypedId<UserId>): TryEither<Flow<Portfolio>> =
      sqlLoader.loadSql(FIND_ALL_FOR_USER_SQL).mapCatch { sql ->
        databaseClient
            .sql(sql)
            .bind("userId", userId.value)
            .map(portfolioRowMapper)
            .all()
            .toFlux()
            .asFlow()
            .flatMapConcat { flow { it } } // TODO why does this make things compile???
      }

  private suspend fun createAsBatch(
      portfolios: List<Portfolio>
  ): suspend (String) -> TryEither<List<Long>> = { sql ->
    Either.catch {
      databaseClient
          .inConnectionMany { conn ->
            val statement = conn.createStatement(sql)
            portfolios
                .toSqlBatches(statement) { record, stmt ->
                  stmt.bind(0, record.id.value).bind(1, record.userId.value).bind(2, record.name)
                }
                .execute()
                .toFlux()
                .flatMap { result -> result.rowsUpdated.toFlux() }
          }
          .asFlow()
          .toList()
    }
  }
}
