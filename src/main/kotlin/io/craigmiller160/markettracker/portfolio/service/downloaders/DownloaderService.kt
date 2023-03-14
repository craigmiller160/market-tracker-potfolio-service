package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned

interface DownloaderService {
  suspend fun download(): List<SharesOwned>
}
