package io.craigmiller160.markettracker.portfolio.routes

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRoute {
  @Bean fun practice(handler: PracticeHandler) = coRouter { GET("/practice", handler::practice) }
}
