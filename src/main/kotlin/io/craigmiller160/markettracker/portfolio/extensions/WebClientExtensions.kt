package io.craigmiller160.markettracker.portfolio.extensions

import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import java.lang.RuntimeException
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

suspend inline fun <reified T : Any> WebClient.ResponseSpec.awaitBodyResult(): KtResult<T> =
    ktRunCatching {
      awaitBody<T>()
    }

private fun responseToException(stackTraceSource: Throwable): (ClientResponse) -> Mono<Throwable> =
    { response ->
      WebClientResponseException.create(
              response.statusCode().value(),
              response.statusCode().toString(),
              response.headers().asHttpHeaders(),
              "".toByteArray(),
              null)
          .apply { stackTrace = stackTraceSource.stackTrace }
          .let { Mono.just(it) }
    }

fun WebClient.RequestHeadersSpec<*>.retrieveSuccess(): ResponseSpec {
  val stackTraceSource = RuntimeException()
  return retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, responseToException(stackTraceSource))
      .onStatus(HttpStatusCode::is5xxServerError, responseToException(stackTraceSource))
}
