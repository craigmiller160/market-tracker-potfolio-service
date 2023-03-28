package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.r2dbc.spi.RowMetadata
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

private typealias SharesOwnedData = Pair<Map<String, Any?>, TryEither<SharesOwned>>

class SharesOwnedRowMapperTest {
  companion object {
    @JvmStatic
    fun sharesOwnedData(): Stream<SharesOwnedData> {
      TODO()
    }
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
