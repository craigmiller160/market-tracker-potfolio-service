package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import org.springframework.stereotype.Service

@Service
class CraigMillerDownloaderService401k : ChildDownloaderService {
  override suspend fun download(token: String): ChildDownloadServiceResult {
    TODO("Not yet implemented")
  }
}
