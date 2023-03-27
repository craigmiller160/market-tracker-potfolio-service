package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.mockk.mockk
import io.r2dbc.spi.RowMetadata
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

typealias PortfolioData = Pair<Map<String, Any?>, TryEither<Portfolio>>

class PortfolioRowMapperTest {
  companion object {
    @JvmStatic
    fun portfolioData(): Stream<PortfolioData> {
      val id = UUID.randomUUID()
      val userId = UUID.randomUUID()
      val name = "Bob"
      val base = mapOf("id" to id, "user_id" to userId, "name" to name)
      val portfolio = BasePortfolio(id = TypedId(id), userId = TypedId(userId), name = name)
      return Stream.of(
          base to Either.Right(portfolio),
          base + mapOf("id" to null) to
              Either.Left(NullPointerException("Value is null: Missing id column")),
          base + mapOf("user_id" to null) to
              Either.Left(NullPointerException("Value is null: Missing user_id column")),
          base + mapOf("name" to null) to
              Either.Left(NullPointerException("Value is null: Missing name column")),
          base + mapOf("id" to "Hello") to
              Either.Left(ClassCastException("Invalid type: java.util.UUID")))
    }
  }

  private val metadata: RowMetadata = mockk()

  @ParameterizedTest
  @MethodSource("portfolioData")
  fun `maps Portfolio from row`(data: PortfolioData) {
    val (map, expected) = data
    val row = MockRow(map)
    val actual = portfolioRowMapper(row, metadata)
    when (expected) {
      is Either.Right -> actual.shouldBeRight(expected.value)
      is Either.Left -> actual.shouldBeLeft(expected.value)
    }
  }
}
