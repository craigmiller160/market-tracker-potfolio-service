package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.KtResult

interface DownloaderService {
  suspend fun download(): KtResult<List<SharesOwned>>
}
