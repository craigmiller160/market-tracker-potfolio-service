package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService : DownloaderService {
  companion object {
    const val GOOGLE_SHEETS_API_BASE_URL = "https://sheets.googleapis.com/v4"
  }

  private val webClient = WebClient.create(GOOGLE_SHEETS_API_BASE_URL)
  override suspend fun download(): Flow<SharesOwned> {
    TODO("Not yet implemented")
  }
}
