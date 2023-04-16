package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.coFlatMap
import org.springframework.stereotype.Repository

@Repository
class DatabaseClientPortfolioRepository(
    private val databaseClient: CoroutineDatabaseClient,
    private val sqlLoader: SqlLoader
) : PortfolioRepository {
  companion object {
    private const val INSERT_PORTFOLIO_SQL = "portfolio/insertPortfolio.sql"
    private const val INSERT_PORTFOLIO_BATCH_SQL = "portfolio/insertPortfolioBatch.sql"
    private const val FIND_ALL_FOR_USER_SQL = "portfolio/findAllForUser.sql"
    private const val DELETE_ALL_PORTFOLIOS_SQL = "portfolio/deleteAllPortfoliosForUserIds.sql"
  }

  override suspend fun createPortfolio(portfolio: Portfolio): TryEither<Portfolio> {
    val params =
        mapOf(
            "id" to portfolio.id.value,
            "userId" to portfolio.userId.value,
            "name" to portfolio.name)
    return sqlLoader
        .loadSql(INSERT_PORTFOLIO_SQL)
        .flatMap { sql -> databaseClient.update(sql, params) }
        .map { portfolio }
  }

  override suspend fun createAllPortfolios(
      portfolios: List<Portfolio>
  ): TryEither<List<Portfolio>> =
      sqlLoader.loadSql(INSERT_PORTFOLIO_BATCH_SQL).coFlatMap(createAsBatch(portfolios)).map {
        portfolios
      }

  override suspend fun findAllForUser(userId: TypedId<UserId>): TryEither<List<Portfolio>> {
    val params = mapOf("userId" to userId.value)
    return sqlLoader.loadSql(FIND_ALL_FOR_USER_SQL).flatMap { sql ->
      databaseClient.query(sql, params)
      TODO()
    }
  }

  override suspend fun deleteAllPortfoliosForUsers(
      userIds: List<TypedId<UserId>>
  ): TryEither<Unit> {
    val params = mapOf("userIds" to userIds.map { it.value })
    return sqlLoader.loadSql(DELETE_ALL_PORTFOLIOS_SQL).flatMap { sql ->
      databaseClient.update(sql, params).map { Unit }
    }
  }

  private suspend fun createAsBatch(
      portfolios: List<Portfolio>
  ): suspend (String) -> TryEither<List<Long>> = { sql ->
    val paramBatches =
        portfolios.map { portfolio ->
          listOf(portfolio.id.value, portfolio.userId.value, portfolio.name)
        }

    databaseClient.batchUpdate(sql, paramBatches)
  }
}
