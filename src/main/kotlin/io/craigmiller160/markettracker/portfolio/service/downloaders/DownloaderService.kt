package io.craigmiller160.markettracker.portfolio.service.downloaders

interface DownloaderService {
  suspend fun download()
}
