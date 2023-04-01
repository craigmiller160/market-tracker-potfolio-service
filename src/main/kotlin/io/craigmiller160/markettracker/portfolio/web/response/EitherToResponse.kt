package io.craigmiller160.markettracker.portfolio.web.response

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun <T : Any> TryEither<T>.toResponse(
    status: Int = 200,
    customize: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ServerResponse =
    when (this) {
      is Either.Right<T> ->
          ServerResponse.status(status)
              .customize()
              .body(BodyInserters.fromValue(value))
              .awaitSingle()
      is Either.Left<Throwable> -> throw value
    }
