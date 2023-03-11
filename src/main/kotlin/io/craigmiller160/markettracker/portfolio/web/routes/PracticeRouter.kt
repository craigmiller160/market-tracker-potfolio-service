package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRouter {
  @Bean
  fun practiceRoutes(handler: PracticeHandler): RouterFunction<ServerResponse> = coRouter {
    GET("/router/practice", handler::helloUniverse)
  }
}
