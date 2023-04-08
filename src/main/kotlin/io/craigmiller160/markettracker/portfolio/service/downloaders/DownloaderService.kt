package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import java.time.LocalDate

interface DownloaderService {
  companion object {
    val MAX_DATE: LocalDate = LocalDate.of(2100, 1, 1)
  }
  suspend fun download(): TryEither<List<PortfolioWithHistory>>
}
