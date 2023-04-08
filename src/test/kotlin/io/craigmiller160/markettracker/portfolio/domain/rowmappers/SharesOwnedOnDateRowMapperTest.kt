package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Stream

private typealias Data = Pair<Map<String, Any?>, TryEither<SharesOwnedOnDate>>

class SharesOwnedOnDateRowMapperTest {
  companion object {
    @JvmStatic
    fun data(): Stream<Data> {
      val userId = UUID.randomUUID()
      val portfolioId = UUID.randomUUID()
      val date = LocalDate.of(2020, 1, 1)
      val symbol = "VTI"
      val totalShares = BigDecimal("10")

      val base =
          mapOf(
              "userId" to userId, "date" to date, "symbol" to symbol, "totalShares" to totalShares)
      val portfolioBase = base + mapOf("portfolioId" to portfolioId)

      val record =
          SharesOwnedOnDate(
              userId = TypedId(userId), date = date, symbol = symbol, totalShares = totalShares)
      val portfolioRecord = record.copy(portfolioId = TypedId(portfolioId))
      return Stream.of(base to Either.Right(record), portfolioBase to Either.Right(portfolioRecord))
    }
  }
}
