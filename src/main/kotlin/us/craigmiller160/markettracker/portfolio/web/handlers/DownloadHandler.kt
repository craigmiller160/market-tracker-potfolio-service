package io.craigmiller160.markettracker.portfolio.web.handlers

import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderOrchestrationService
import io.craigmiller160.markettracker.portfolio.web.response.toResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class DownloadHandler(private val downloaderOrchestrationService: DownloaderOrchestrationService) {
  suspend fun download(request: ServerRequest): ServerResponse =
      downloaderOrchestrationService.download().toResponse(204)
}
