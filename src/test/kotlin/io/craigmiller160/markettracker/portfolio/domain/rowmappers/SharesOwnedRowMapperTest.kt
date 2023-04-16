package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.toDateRange
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SharesOwnedRowMapperTest {
  companion object {
    @JvmStatic
    fun sharesOwnedData(): Stream<RowMapperData<SharesOwned>> {
      val id = UUID.randomUUID()
      val userId = UUID.randomUUID()
      val portfolioId = UUID.randomUUID()
      val dateRangeStart = LocalDate.of(2020, 1, 1)
      val dateRangeEnd = LocalDate.of(2020, 2, 1)
      val symbol = "VTI"
      val totalShares = BigDecimal("10")
      val base =
          mapOf(
              "id" to id,
              "user_id" to userId,
              "portfolio_id" to portfolioId,
              "date_range" to SharesOwned.toDateRange(dateRangeStart, dateRangeEnd),
              "symbol" to symbol,
              "total_shares" to totalShares)
      val sharesOwned =
          SharesOwned(
              id = TypedId(id),
              userId = TypedId(userId),
              portfolioId = TypedId(portfolioId),
              dateRangeStart = dateRangeStart,
              dateRangeEnd = dateRangeEnd,
              symbol = symbol,
              totalShares = totalShares)
      return Stream.of(
          base to Either.Right(sharesOwned),
          base + mapOf("id" to null) to nullLeft("id"),
          base + mapOf("user_id" to null) to nullLeft("user_id"),
          base + mapOf("portfolio_id" to null) to nullLeft("portfolio_id"),
          base + mapOf("date_range" to null) to nullLeft("date_range"),
          base + mapOf("symbol" to null) to nullLeft("symbol"),
          base + mapOf("total_shares" to null) to nullLeft("total_shares"),
          base + mapOf("id" to 1) to typeLeft("id", UUID::class),
          base + mapOf("user_id" to 1) to typeLeft("user_id", UUID::class),
          base + mapOf("portfolio_id" to 1) to typeLeft("portfolio_id", UUID::class),
          base + mapOf("date_range" to 1) to typeLeft("date_range", String::class),
          base + mapOf("symbol" to 1) to typeLeft("symbol", String::class),
          base + mapOf("total_shares" to 1) to typeLeft("total_shares", BigDecimal::class))
    }
  }

  @ParameterizedTest
  @MethodSource("sharesOwnedData")
  fun `maps SharesOwned from row`(data: RowMapperData<SharesOwned>) {
    validateRowMapper(sharesOwnedRowMapper, data)
  }
}
