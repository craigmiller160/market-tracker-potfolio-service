package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import kotlinx.coroutines.reactor.awaitSingle
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRoutesHolder(private val handler: PracticeHandler) {
  @Bean
  @RouterOperation(beanClass = PracticeHandler::class, beanMethod = "helloUniverse")
  fun practiceRoutes(handler: PracticeHandler): RouterFunction<ServerResponse> = coRouter {
    GET("/router/practice", handler::helloUniverse)
    GET("/router/practice3/{name}", ::helloName)
  }

  private suspend fun helloName(request: ServerRequest): ServerResponse =
      with(request) { request.pathVariable("name") }
          .let { name -> handler.helloName(name) }
          .let { ServerResponse.ok().body(BodyInserters.fromValue(it)).awaitSingle() }

  @Bean
  fun practiceRoutes2(handler: PracticeHandler): RouterFunction<ServerResponse> {
    return route()
        .GET("/router/practice2", handler::helloUniverseMono) { ops ->
          ops.operationId("getPractice2")
              .beanClass(PracticeHandler::class.java)
              .beanMethod("helloUniverseMono")
        }
        .build()
  }
}
