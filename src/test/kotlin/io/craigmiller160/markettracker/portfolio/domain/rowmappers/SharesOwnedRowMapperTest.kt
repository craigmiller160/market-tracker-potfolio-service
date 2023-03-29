package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.toDateRange
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.r2dbc.spi.RowMetadata
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

private typealias SharesOwnedData = Pair<Map<String, Any?>, TryEither<SharesOwned>>

class SharesOwnedRowMapperTest {
  companion object {
    @JvmStatic
    fun sharesOwnedData(): Stream<SharesOwnedData> {
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
          base + mapOf("id" to 1) to typeLeft("id", UUID::class.java),
          base + mapOf("user_id" to 1) to typeLeft("user_id", UUID::class.java),
          base + mapOf("portfolio_id" to 1) to typeLeft("portfolio_id", UUID::class.java),
          base + mapOf("date_range" to 1) to typeLeft("date_range", String::class.java),
          base + mapOf("symbol" to 1) to typeLeft("symbol", String::class.java),
          base + mapOf("total_shares" to 1) to typeLeft("total_shares", BigDecimal::class.java))
    }

    private fun typeLeft(column: String, type: Class<*>): Either<Throwable, SharesOwned> =
        Either.Left(
            IllegalArgumentException(
                "Error getting column $column", ClassCastException("Invalid type: ${type.name}")))

    private fun nullLeft(column: String): Either<Throwable, SharesOwned> =
        Either.Left(NullPointerException("Value is null: Missing $column column"))
  }

  private val metadata: RowMetadata = mockk()
  @ParameterizedTest
  @MethodSource("sharesOwnedData")
  fun `maps SharesOwned from row`(data: SharesOwnedData) {
    val (map, expected) = data
    val row = MockRow(map)
    val actual = sharesOwnedRowMapper(row, metadata)
    when (expected) {
      is Either.Right -> actual.shouldBeRight(expected.value)
      is Either.Left -> {
        val actualException = actual.shouldBeLeft(expected.value)
        expected.value.cause?.let { expectedCause ->
          val actualCause = actualException.cause.shouldNotBeNull()
          actualCause.javaClass.shouldBe(expectedCause.javaClass)
          actualCause.message.shouldBe(expectedCause.message)
        }
      }
    }
  }
}
