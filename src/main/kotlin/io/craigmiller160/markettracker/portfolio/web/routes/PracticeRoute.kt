package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRoute {
  @Bean fun practice(handler: PracticeHandler) = coRouter { GET("/practice", handler::practice) }
}
