package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class DatabaseClientSharesOwnedRepository(
    private val databaseClient: DatabaseClient,
    private val sqlLoader: SqlLoader
) : SharesOwnedRepository {
  companion object {
    private const val INSERT_SHARES_OWNED_SQL = "sharesOwned/insertSharesOwned.sql"
  }
  override suspend fun createAllSharesOwned(
      sharesOwned: List<SharesOwned>
  ): KtResult<List<SharesOwned>> {
    TODO("Not yet implemented")
  }
}
