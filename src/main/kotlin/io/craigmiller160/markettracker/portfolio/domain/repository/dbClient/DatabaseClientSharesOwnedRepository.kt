package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.dateRange
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.coFlatMap
import io.craigmiller160.markettracker.portfolio.extensions.toSqlBatches
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.kotlin.core.publisher.toFlux

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
  ): TryEither<List<SharesOwned>> =
      sqlLoader.loadSql(INSERT_SHARES_OWNED_SQL).coFlatMap(createAsBatch(sharesOwned)).map {
        sharesOwned
      }

  private suspend fun createAsBatch(
      sharesOwned: List<SharesOwned>
  ): suspend (String) -> KtResult<List<Long>> = { sql ->
    ktRunCatching {
      databaseClient
          .inConnectionMany { conn ->
            val statement = conn.createStatement(sql)
            sharesOwned
                .toSqlBatches(statement) { record, stmt ->
                  stmt
                      .bind(0, record.id.value)
                      .bind(1, record.userId.value)
                      .bind(2, record.portfolioId.value)
                      .bind(3, record.dateRange)
                      .bind(4, record.symbol)
                      .bind(5, record.totalShares)
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
