package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRoute {
  @Bean
  @RouterOperations(value = [RouterOperation(path = "/practice", method = [RequestMethod.GET])])
  fun practice(handler: PracticeHandler) = coRouter { GET("/practice", handler::practice) }
}
