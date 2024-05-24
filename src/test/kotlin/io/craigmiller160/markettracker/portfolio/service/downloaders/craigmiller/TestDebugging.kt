package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import java.nio.file.Files
import java.nio.file.Paths

private val objectMapper = jacksonObjectMapper()

fun writeDataForDebugging(
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
