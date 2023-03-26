package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.dateRange
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
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
  ): KtResult<List<SharesOwned>> =
      sqlLoader
          .loadSql(INSERT_SHARES_OWNED_SQL)
          .flatMap { sql ->
            ktRunCatching {
              databaseClient
                  .inConnectionMany { conn ->
                    val stmt = conn.createStatement(sql)
                    sharesOwned.forEach { record ->
                      stmt
                          .bind(0, record.id.value)
                          .bind(1, record.userId.value)
                          .bind(2, record.portfolioId.value)
                          .bind(3, record.dateRange)
                          .bind(4, record.symbol)
                          .bind(5, record.totalShares)
                          .add()
                    }
                    stmt.execute().toFlux()
                  }
                  .asFlow()
                  .toList() // TODO is there a better option?
            }
          }
          .map { sharesOwned }
}
