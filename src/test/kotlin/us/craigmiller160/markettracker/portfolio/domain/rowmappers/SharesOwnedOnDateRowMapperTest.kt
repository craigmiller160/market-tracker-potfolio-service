package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SharesOwnedOnDateRowMapperTest {
  companion object {
    @JvmStatic
    fun data(): Stream<RowMapperData<SharesOwnedOnDate>> {
      val userId = UUID.randomUUID()
      val portfolioId = UUID.randomUUID()
      val date = LocalDate.of(2020, 1, 1)
      val symbol = "VTI"
      val totalShares = BigDecimal("10")

      val base =
          mapOf(
              "user_id" to userId,
              "date" to date,
              "symbol" to symbol,
              "total_shares" to totalShares)
      val portfolioBase = base + mapOf("portfolio_id" to portfolioId)

      val record =
          SharesOwnedOnDate(
              userId = TypedId(userId), date = date, symbol = symbol, totalShares = totalShares)
      val portfolioRecord = record.copy(portfolioId = TypedId(portfolioId))
      return Stream.of(
          base to Either.Right(record),
          portfolioBase to Either.Right(portfolioRecord),
          base + mapOf("user_id" to null) to nullLeft("user_id"),
          base + mapOf("date" to null) to nullLeft("date"),
          base + mapOf("symbol" to null) to nullLeft("symbol"),
          base + mapOf("total_shares" to null) to nullLeft("total_shares"),
          base + mapOf("user_id" to 123) to typeLeft("user_id", UUID::class),
          base + mapOf("date" to 123) to typeLeft("date", LocalDate::class),
          base + mapOf("symbol" to 123) to typeLeft("symbol", String::class),
          base + mapOf("total_shares" to "hello") to typeLeft("total_shares", BigDecimal::class))
    }
  }

  @ParameterizedTest
  @MethodSource("data")
  fun `maps SharesOwnedOnDate from row`(data: RowMapperData<SharesOwnedOnDate>) {
    validateRowMapper(sharesOwnedOnDateRowMapper, data)
  }
}
