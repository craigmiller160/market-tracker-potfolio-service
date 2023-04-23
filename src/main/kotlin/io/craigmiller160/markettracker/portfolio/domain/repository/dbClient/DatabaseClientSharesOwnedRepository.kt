package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import arrow.core.flatMap
import arrow.core.sequence
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.client.paramsBuilder
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import io.craigmiller160.markettracker.portfolio.domain.models.dateRange
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.currentSharesOwnedRowMapper
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.sharesOwnedOnDateRowMapper
import io.craigmiller160.markettracker.portfolio.domain.sql.SqlLoader
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.coFlatMap
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
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
    private const val FIND_CURRENT_SHARES_OWNED_FOR_STOCK_SQL =
        "sharesOwned/findCurrentSharesOwnedForStock.sql"
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
    val paramBatches =
        sharesOwned.map { record ->
          listOf(
              record.id.value,
              record.userId.value,
              record.portfolioId.value,
              record.dateRange,
              record.symbol,
              record.totalShares)
        }
    databaseClient.batchUpdate(sql, paramBatches)
  }

  override suspend fun findUniqueStocksInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>> {
    val params = paramsBuilder {
      this + ("userId" to userId.value)
      this + ("portfolioId" to portfolioId.value)
    }
    return sqlLoader
        .loadSql(FIND_UNIQUE_STOCKS_SQL)
        .flatMap { sql -> databaseClient.query(sql, params) }
        .flatMap { list -> list.map { it.getRequired("symbol", String::class) }.sequence() }
  }

  override suspend fun findUniqueStocksForUser(userId: TypedId<UserId>): TryEither<List<String>> {
    val params = paramsBuilder { this + ("userId" to userId.value) }
    return sqlLoader
        .loadSql(FIND_UNIQUE_STOCKS_SQL)
        .flatMap { sql -> databaseClient.query(sql, params) }
        .flatMap { list -> list.map { it.getRequired("symbol", String::class) }.sequence() }
  }

  override suspend fun getSharesOwnedAtIntervalInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>> {
    val params = paramsBuilder {
      this + ("userId" to userId.value)
      this + ("symbol" to stockSymbol)
      this + ("portfolioId" to portfolioId.value)
      this + ("startDate" to startDate)
      this + ("endDate" to endDate)
      this + ("interval" to interval.sql)
    }

    return sqlLoader.loadSql(GET_SHARES_OWNED_AT_INTERVAL_SQL).flatMap { sql ->
      databaseClient.query(sql, sharesOwnedOnDateRowMapper, params)
    }
  }

  override suspend fun getSharesOwnedAtIntervalForUser(
      userId: TypedId<UserId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>> {
    val params = paramsBuilder {
      this + ("userId" to userId.value)
      this + ("symbol" to stockSymbol)
      this + ("startDate" to startDate)
      this + ("endDate" to endDate)
      this + ("interval" to interval.sql)
    }

    return sqlLoader.loadSql(GET_SHARES_OWNED_AT_INTERVAL_SQL).flatMap { sql ->
      databaseClient.query(sql, sharesOwnedOnDateRowMapper, params)
    }
  }

  override suspend fun deleteAllSharesOwnedForUsers(
      userIds: List<TypedId<UserId>>
  ): TryEither<Unit> {
    val params = paramsBuilder { this + ("userIds" to userIds.map { it.value }) }
    return sqlLoader
        .loadSql(DELETE_ALL_SHARES_OWNED_SQL)
        .flatMap { sql -> databaseClient.update(sql, params) }
        .map { Unit }
  }

  override suspend fun getCurrentSharesOwnedForStockInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String
  ): TryEither<BigDecimal> {
    val params = paramsBuilder {
      this + ("userId" to userId.value)
      this + ("portfolioId" to portfolioId.value)
      this + ("symbol" to stockSymbol)
    }
    return sqlLoader
        .loadSql(FIND_CURRENT_SHARES_OWNED_FOR_STOCK_SQL)
        .flatMap { sql -> databaseClient.query(sql, currentSharesOwnedRowMapper, params) }
        .map { it.firstOrNull() ?: BigDecimal("0") }
  }

  override suspend fun getCurrentSharesOwnedForStockForUser(
      userId: TypedId<UserId>,
      stockSymbol: String
  ): TryEither<BigDecimal> {
    val params = paramsBuilder {
      this + ("userId" to userId.value)
      this + ("symbol" to stockSymbol)
      this + ("portfolioId" to nullValue<UUID>())
    }
    return sqlLoader
        .loadSql(FIND_CURRENT_SHARES_OWNED_FOR_STOCK_SQL)
        .flatMap { sql -> databaseClient.query(sql, currentSharesOwnedRowMapper, params) }
        .map { it.firstOrNull() ?: BigDecimal("0") }
  }
}
