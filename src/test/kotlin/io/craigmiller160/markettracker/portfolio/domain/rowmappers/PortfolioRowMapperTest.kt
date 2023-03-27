package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.mockk.mockk
import io.r2dbc.spi.RowMetadata
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

typealias PortfolioData = Pair<Map<String, Any>, TryEither<Portfolio>>

class PortfolioRowMapperTest {
  companion object {
    @JvmStatic
    fun portfolioData(): Stream<PortfolioData> {
      return Stream.of()
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
