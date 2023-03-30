package io.craigmiller160.markettracker.portfolio.web.handlers

import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.WebExceptionHandler

@Component
class GlobalExceptionHandler {
  // TODO figure out how to make this work
  @Bean
  @Order(-2) // TODO relying on order-based logic to solve my problem, not ideal
  fun exceptionHandler(): WebExceptionHandler = WebExceptionHandler { exchange, ex ->
    exchange.response.apply { statusCode = HttpStatus.BAD_REQUEST }.setComplete()
  }
}
