package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class CraigMillerDownloaderService : DownloaderService {
  override suspend fun download(): Flow<SharesOwned> {
    TODO("Not yet implemented")
  }
}
