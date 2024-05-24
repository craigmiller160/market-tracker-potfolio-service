package io.craigmiller160.markettracker.portfolio.web.handlers

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.web.types.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Component
class GlobalExceptionHandler(private val objectMapper: ObjectMapper) {
  private val log = LoggerFactory.getLogger(javaClass)
  @Bean
  @Order(-2)
  fun exceptionHandler(): WebExceptionHandler = WebExceptionHandler { exchange, exception ->
    log.error(exception.message, exception)

    val (status, message) = getStatusAndMessageFromException(exception)
    val response =
        ErrorResponse(
            method = exchange.request.method.name(),
            uri = exchange.request.uri.toString(),
            message = message,
            status = status.value())

    Either.catch { objectMapper.writeValueAsBytes(response) }
        .map { exchange.response.bufferFactory().wrap(it) }
        .fold({ ex ->
          log.error("Failed to prepare error response", ex)
          exchange.response.apply { statusCode = HttpStatus.INTERNAL_SERVER_ERROR }.setComplete()
        }) { buffer ->
          exchange.response
              .apply {
                statusCode = status
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              }
              .writeWith(Mono.just(buffer))
        }
  }
}

private data class StatusAndMessage(val status: HttpStatus, val message: String)

private fun getStatusAndMessageFromException(ex: Throwable): StatusAndMessage =
    ex.javaClass.getAnnotation(ResponseStatus::class.java)?.let { annotation ->
      StatusAndMessage(status = annotation.code, message = "${annotation.reason}: ${ex.message}")
    }
        ?: StatusAndMessage(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "")
