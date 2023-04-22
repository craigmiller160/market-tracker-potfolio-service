package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import java.math.BigDecimal
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CurrentSharesOwnedRowMapperTest {
  companion object {
    @JvmStatic
    fun currentSharesOwnedData(): Stream<RowMapperData<BigDecimal>> {
      val value = BigDecimal("10")
      val base = mapOf("the_sum" to value)
      return Stream.of(
          base to Either.Right(value),
          base + mapOf("the_sum" to null) to Either.Right(BigDecimal("0")),
          mapOf("the_sum" to 11) to typeLeft("the_sum", BigDecimal::class))
    }
  }

  @ParameterizedTest
  @MethodSource("currentSharesOwnedData")
  fun `maps current shares owned from row`(data: RowMapperData<BigDecimal>) {
    validateRowMapper(currentSharesOwnedRowMapper, data)
  }
}
