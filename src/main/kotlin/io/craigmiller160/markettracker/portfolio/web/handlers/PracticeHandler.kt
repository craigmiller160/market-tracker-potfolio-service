package io.craigmiller160.markettracker.portfolio.web.handlers

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class PracticeHandler {
  suspend fun helloUniverse(request: ServerRequest): ServerResponse =
      ServerResponse.ok().body(BodyInserters.fromValue("Hello Universe")).awaitSingle()

  fun helloUniverseMono(request: ServerRequest): Mono<ServerResponse> =
      ServerResponse.ok().body(BodyInserters.fromValue("Hello Universe"))
}
