package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.r2dbc.spi.RowMetadata
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

private typealias PortfolioData = Pair<Map<String, Any?>, TryEither<Portfolio>>

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
          base + mapOf("id" to null) to nullLeft("id"),
          base + mapOf("user_id" to null) to nullLeft("user_id"),
          base + mapOf("name" to null) to nullLeft("name"),
          base + mapOf("id" to "Hello") to typeLeft("id", UUID::class.java),
          base + mapOf("user_id" to "Hello") to typeLeft("user_id", UUID::class.java),
          base + mapOf("name" to 123) to typeLeft("name", String::class.java))
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
