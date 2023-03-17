package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.functions.KtResult

interface DownloaderService {
  suspend fun download(): KtResult<List<PortfolioWithHistory>>
}
