package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Paths

fun writeDataForDebugging(
    objectMapper: ObjectMapper,
    prefix: String,
    index: Int,
    expected: List<SharesOwned>,
    actual: List<SharesOwned>
) {
  val outputPath = Paths.get(System.getProperty("user.dir"), "build", "craigmiller_download")
  Files.createDirectories(outputPath)
  Files.write(
      outputPath.resolve("${prefix}_expected$index.json"),
      objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(expected))
  Files.write(
      outputPath.resolve("${prefix}_actual$index.json"),
      objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(actual))
}

fun validateSharesOwned(expected: List<SharesOwned>, actual: List<SharesOwned>) {
  actual.shouldHaveSize(expected.size).forEachIndexed { index, actual ->
    Either.catch {
          val expected = expected[index]
          actual.userId.shouldBe(expected.userId)
          actual.portfolioId.shouldBe(expected.portfolioId)
          actual.dateRangeStart.shouldBe(expected.dateRangeStart)
          actual.dateRangeEnd.shouldBe(expected.dateRangeEnd)
          actual.symbol.shouldBe(expected.symbol)
          actual.totalShares.shouldBeEqualComparingTo(expected.totalShares)
        }
        .shouldBeRight { ex ->
          ex.printStackTrace()
          "Error validating record $index: ${ex.message}"
        }
  }
}
