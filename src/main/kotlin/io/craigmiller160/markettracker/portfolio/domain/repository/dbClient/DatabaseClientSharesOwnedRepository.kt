package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.dateRange
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.kotlin.core.publisher.toMono

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
              databaseClient
                  .inConnection { conn ->
                    val stmt = conn.createStatement(sql)
                    // TODO definitely refactor to be flexible
                    stmt
                        .bind(0, sharesOwned[0].id.value)
                        .bind(1, sharesOwned[0].userId.value)
                        .bind(2, sharesOwned[0].portfolioId.value)
                        .bind(3, sharesOwned[0].dateRange)
                        .bind(4, sharesOwned[0].symbol)
                        .bind(5, sharesOwned[0].totalShares)
                        .add()
                        .bind(0, sharesOwned[1].id.value)
                        .bind(1, sharesOwned[1].userId.value)
                        .bind(2, sharesOwned[1].portfolioId.value)
                        .bind(3, sharesOwned[1].dateRange)
                        .bind(4, sharesOwned[1].symbol)
                        .bind(5, sharesOwned[1].totalShares)
                    stmt.execute().toMono()
                  }
                  .awaitSingle()
            }
          }
          .map { result -> println("ROWS: ${result.rowsUpdated.awaitSingle()}") }
          .map { sharesOwned }
}
