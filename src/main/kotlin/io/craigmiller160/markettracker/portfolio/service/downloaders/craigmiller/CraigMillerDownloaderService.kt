package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService(
    private val craigMillerDownloaderConfig: CraigMillerDownloaderConfig,
    private val objectMapper: ObjectMapper
) : DownloaderService {

  private val webClient = WebClient.create(craigMillerDownloaderConfig.googleSheetsApiBaseUrl)
  private val dataUri =
      "/spreadsheets/${craigMillerDownloaderConfig.spreadsheetId}/values/${craigMillerDownloaderConfig.valuesRange}"
  override suspend fun download(): Flow<SharesOwned> {
    TODO("Not yet implemented")
  }

  private suspend fun readServiceAccount(): GoogleApiServiceAccount =
      withContext(Dispatchers.IO) {
        craigMillerDownloaderConfig.serviceAccountJsonPath
        TODO()
      }
}
