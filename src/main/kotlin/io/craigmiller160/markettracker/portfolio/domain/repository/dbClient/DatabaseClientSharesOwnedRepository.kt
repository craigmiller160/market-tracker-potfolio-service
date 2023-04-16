package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.Either
import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.coFlatMap
import io.craigmiller160.markettracker.portfolio.extensions.mapCatch
import java.time.LocalDate
import org.springframework.stereotype.Repository

@Repository
class DatabaseClientSharesOwnedRepository(
    private val databaseClient: CoroutineDatabaseClient,
    private val sqlLoader: SqlLoader
) : SharesOwnedRepository {
  companion object {
    private const val INSERT_SHARES_OWNED_SQL = "sharesOwned/insertSharesOwnedBatch.sql"
    private const val FIND_UNIQUE_STOCKS_SQL = "sharesOwned/findUniqueStocks.sql"
    private const val GET_SHARES_OWNED_AT_INTERVAL_SQL = "sharesOwned/getSharesOwnedAtInterval.sql"
    private const val DELETE_ALL_SHARES_OWNED_SQL = "sharesOwned/deleteAllSharesOwnedForUsers.sql"
  }
  override suspend fun createAllSharesOwned(
      sharesOwned: List<SharesOwned>
  ): TryEither<List<SharesOwned>> =
      sqlLoader.loadSql(INSERT_SHARES_OWNED_SQL).coFlatMap(createAsBatch(sharesOwned)).map {
        sharesOwned
      }

  private suspend fun createAsBatch(
      sharesOwned: List<SharesOwned>
  ): suspend (String) -> TryEither<List<Long>> = { sql ->
    Either.catch {
      //      databaseClient
      //          .inConnectionMany { conn ->
      //            val statement = conn.createStatement(sql)
      //            sharesOwned
      //                .toSqlBatches(statement) { record, stmt ->
      //                  stmt
      //                      .bind(0, record.id.value)
      //                      .bind(1, record.userId.value)
      //                      .bind(2, record.portfolioId.value)
      //                      .bind(3, record.dateRange)
      //                      .bind(4, record.symbol)
      //                      .bind(5, record.totalShares)
      //                }
      //                .execute()
      //                .toFlux()
      //                .flatMap { result -> result.rowsUpdated.toFlux() }
      //          }
      //          .asFlow()
      //          .toList()
      TODO()
    }
  }

  override suspend fun findUniqueStocksInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>> = TODO()
  //      sqlLoader
  //          .loadSqlMustacheTemplate(FIND_UNIQUE_STOCKS_SQL)
  //          .flatMap { template -> template.executeWithSectionsEnabled("portfolioId") }
  //          .mapCatch { sql ->
  //            databaseClient
  //                .sql(sql)
  //                .bind("userId", userId.value)
  //                .bind("portfolioId", portfolioId.value)
  //                .map { row -> row.get("symbol")?.toString() }
  //                .all()
  //                .toIterable()
  //                .toList()
  //                .filterNotNull()
  //          }

  override suspend fun findUniqueStocksForUser(userId: TypedId<UserId>): TryEither<List<String>> =
      sqlLoader
          .loadSqlMustacheTemplate(FIND_UNIQUE_STOCKS_SQL)
          .flatMap { template -> template.executeWithSectionsEnabled() }
          .mapCatch { sql ->
            //            databaseClient
            //                .sql(sql)
            //                .bind("userId", userId.value)
            //                .map { row -> row.get("symbol")?.toString() }
            //                .all()
            //                .toIterable()
            //                .toList()
            //                .filterNotNull()
            TODO()
          }

  override suspend fun getSharesOwnedAtIntervalInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>> = TODO()
  //      sqlLoader
  //          .loadSqlMustacheTemplate(GET_SHARES_OWNED_AT_INTERVAL_SQL)
  //          .flatMap { template -> template.executeWithSectionsEnabled("portfolioId") }
  //          .mapCatch { sql ->
  //            databaseClient
  //                .sql(sql)
  //                .bind("userId", userId.value)
  //                .bind("symbol", stockSymbol)
  //                .bind("portfolioId", portfolioId.value)
  //                .bind("startDate", startDate)
  //                .bind("endDate", endDate)
  //                .bind("interval", interval.sql)
  //                .map(sharesOwnedOnDateRowMapper)
  //                .all()
  //                .asFlow()
  //          }
  //          .coFlatMap { flow -> flow.toList().sequence() }

  override suspend fun getSharesOwnedAtIntervalForUser(
      userId: TypedId<UserId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>> =
      sqlLoader
          .loadSqlMustacheTemplate(GET_SHARES_OWNED_AT_INTERVAL_SQL)
          .flatMap { template -> template.executeWithSectionsEnabled() }
          .mapCatch { sql ->
            //            databaseClient
            //                .sql(sql)
            //                .bind("userId", userId.value)
            //                .bind("symbol", stockSymbol)
            //                .bind("startDate", startDate)
            //                .bind("endDate", endDate)
            //                .bind("interval", interval.sql)
            //                .map(sharesOwnedOnDateRowMapper)
            //                .all()
            //                .asFlow()
            TODO()
          }
  //          .coFlatMap { flow -> flow.toList().sequence() }

  override suspend fun deleteAllSharesOwnedForUsers(
      userIds: List<TypedId<UserId>>
  ): TryEither<Unit> =
      sqlLoader.loadSql(DELETE_ALL_SHARES_OWNED_SQL).mapCatch { sql ->
        //        databaseClient
        //            .sql(sql)
        //            .bind("userIds", userIds.map { it.value })
        //            .fetch()
        //            .rowsUpdated()
        //            .awaitSingle()
        TODO()
      }
}
