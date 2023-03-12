package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService(
    private val craigMillerDownloaderConfig: CraigMillerDownloaderConfig
) : DownloaderService {

  private val webClient = WebClient.create(craigMillerDownloaderConfig.googleSheetsApiBaseUrl)
  override suspend fun download(): Flow<SharesOwned> {
    TODO("Not yet implemented")
  }
}
