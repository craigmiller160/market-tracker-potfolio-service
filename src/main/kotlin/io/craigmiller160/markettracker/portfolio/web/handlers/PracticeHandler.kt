package io.craigmiller160.markettracker.portfolio.web.handlers

import io.craigmiller160.markettracker.portfolio.web.types.PracticeResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PracticeHandler {
  suspend fun practice(request: ServerRequest): ServerResponse =
      ServerResponse.ok()
          .body(BodyInserters.fromValue(PracticeResponse("Hello World")))
          .awaitSingle()
}
