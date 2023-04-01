package io.craigmiller160.markettracker.portfolio.service.downloaders

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import io.kotest.assertions.arrow.core.shouldBeRight
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DownloaderOrchestrationServiceTest {
  private val craigMillerDownloaderService: CraigMillerDownloaderService = mockk()
  private val persistDownloadService: PersistDownloadService = mockk()

  private lateinit var downloaderOrchestrationService: DownloaderOrchestrationService

  @BeforeEach
  fun setup() {
    downloaderOrchestrationService =
        DownloaderOrchestrationService(craigMillerDownloaderService, persistDownloadService)
  }

  @Test
  fun `downloads data and writes to database`() {
    val portfolio =
        PortfolioWithHistory(
            id = TypedId(), userId = TypedId(), name = "Something", ownershipHistory = listOf())

    coEvery { craigMillerDownloaderService.download() } returns Either.Right(listOf(portfolio))
    coEvery { persistDownloadService.persistPortfolios(listOf(portfolio)) } returns
        Either.Right(listOf(portfolio))

    runBlocking { downloaderOrchestrationService.downloadAtInterval() }.shouldBeRight(Unit)
  }
}
