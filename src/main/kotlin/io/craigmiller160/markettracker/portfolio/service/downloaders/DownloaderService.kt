package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import kotlinx.coroutines.flow.Flow

interface DownloaderService {
  suspend fun download(): Flow<SharesOwned>
}
