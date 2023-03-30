package io.craigmiller160.markettracker.portfolio.web.response

import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import org.springframework.web.reactive.function.server.ServerResponse

fun <T> TryEither<T>.toResponse(): ServerResponse {
  TODO()
}
