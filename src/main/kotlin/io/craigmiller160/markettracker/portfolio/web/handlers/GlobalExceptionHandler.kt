package io.craigmiller160.markettracker.portfolio.web.handlers

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.WebExceptionHandler

@Component
class GlobalExceptionHandler {
  @Bean
  @Order(-2)
  fun exceptionHandler(): WebExceptionHandler = WebExceptionHandler { exchange, ex ->
    exchange.response.apply { statusCode = HttpStatus.BAD_REQUEST }.setComplete()
  }
}
