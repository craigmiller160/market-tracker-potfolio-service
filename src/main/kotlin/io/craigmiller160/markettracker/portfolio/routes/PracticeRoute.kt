package io.craigmiller160.markettracker.portfolio.routes

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Component
class PracticeRoute {
  @Bean
  fun practice() = router {
    GET("/practice") { _ -> ServerResponse.ok().body(BodyInserters.fromValue("Hello World")) }
  }
}
