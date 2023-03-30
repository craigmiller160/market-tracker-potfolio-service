package io.craigmiller160.markettracker.portfolio.web.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.web.types.ErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.WebExceptionHandler

@Component
class GlobalExceptionHandler(private val objectMapper: ObjectMapper) {
  @Bean
  @Order(-2)
  fun exceptionHandler(): WebExceptionHandler = WebExceptionHandler { exchange, ex ->
    val status = HttpStatus.INTERNAL_SERVER_ERROR
    val response =
        ErrorResponse(
            method = exchange.request.method.name(),
            uri = exchange.request.uri.toString(),
            message = ex.message ?: "",
            status = status.value())
    exchange.response
        .apply {
          statusCode = status
          headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .writeWith()
  }
}
