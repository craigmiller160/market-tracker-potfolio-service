package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PortfolioRowMapperTest {
  companion object {
    @JvmStatic
    fun portfolioData(): Stream<RowMapperData<Portfolio>> {
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

  @ParameterizedTest
  @MethodSource("portfolioData")
  fun `maps Portfolio from row`(data: RowMapperData<Portfolio>) {
    validateRowMapper(portfolioRowMapper, data)
  }
}
