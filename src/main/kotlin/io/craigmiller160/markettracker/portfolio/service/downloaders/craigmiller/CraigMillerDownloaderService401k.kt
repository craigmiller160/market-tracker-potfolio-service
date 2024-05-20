package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class CraigMillerDownloaderService401k : ChildDownloaderService {
  override suspend fun download(token: String): ChildDownloadServiceResult = coroutineScope {
    async { Either.Right(listOf()) }
  }
}
