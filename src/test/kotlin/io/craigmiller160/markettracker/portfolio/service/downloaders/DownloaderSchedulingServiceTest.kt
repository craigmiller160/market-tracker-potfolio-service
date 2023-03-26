package io.craigmiller160.markettracker.portfolio.service.downloaders

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller.CraigMillerDownloaderService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DownloaderSchedulingServiceTest {
  private val craigMillerDownloaderService: CraigMillerDownloaderService = mockk()
  private val persistDownloadService: PersistDownloadService = mockk()

  private lateinit var downloaderSchedulingService: DownloaderSchedulingService

  @BeforeEach
  fun setup() {
    downloaderSchedulingService =
        DownloaderSchedulingService(craigMillerDownloaderService, persistDownloadService)
  }

  @Test
  fun `downloads data and writes to database`() {
    val portfolio =
        PortfolioWithHistory(
            id = TypedId(), userId = TypedId(), name = "Something", ownershipHistory = listOf())

    coEvery { craigMillerDownloaderService.download() } returns Ok(listOf(portfolio))
    coEvery { persistDownloadService.persistPortfolios(listOf(portfolio)) } returns
        Ok(listOf(portfolio))

    runBlocking { downloaderSchedulingService.downloadAtInterval() }.getOrThrow()
  }
}
