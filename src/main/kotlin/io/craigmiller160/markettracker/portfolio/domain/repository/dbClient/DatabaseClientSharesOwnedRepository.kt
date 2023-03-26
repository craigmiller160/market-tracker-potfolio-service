package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
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
  ): KtResult<List<SharesOwned>> =
      sqlLoader
          .loadSql(INSERT_SHARES_OWNED_SQL)
          .flatMap { sql ->
            ktRunCatching {
              databaseClient.inConnectionMany { conn ->
                val stmt = conn.createStatement(sql)
                sharesOwned.forEach { record ->
                  stmt
                      .bind("id", record.id.value)
                      .bind("userId", record.userId.value)
                      .bind("portfolioId", record.portfolioId.value)
                      .bind("dateRangeStart", record.dateRangeStart)
                      .bind("dateRangeEnd", record.dateRangeEnd)
                      .bind("symbol", record.symbol)
                      .bind("totalShares", record.totalShares)
                      .add()
                }
                stmt.execute().toFlux()
              }
            }
          }
          .map { sharesOwned }
}
