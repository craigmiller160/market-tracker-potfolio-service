package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import io.craigmiller160.markettracker.portfolio.web.types.PracticeResponse
import io.swagger.v3.oas.annotations.enums.ParameterIn
import kotlinx.coroutines.reactor.awaitSingle
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springdoc.core.fn.builders.parameter.Builder
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class PracticeRoutesHolder(private val handler: PracticeHandler) {
  @Bean
  @RouterOperations(
      value =
          [
              RouterOperation(beanClass = PracticeHandler::class, beanMethod = "helloUniverse"),
              RouterOperation(
                  beanClass = PracticeHandler::class,
                  beanMethod = "helloName",
                  path = "/router/practice3/{name}",
                  method = [RequestMethod.GET])])
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
        .GET("/router/practice2/{param}", handler::helloUniverseMono) { ops ->
          ops.operationId("getPractice2")
              .parameter(Builder.parameterBuilder().name("param").`in`(ParameterIn.PATH))
              .parameter(Builder.parameterBuilder().name("queryParam").`in`(ParameterIn.QUERY))
        }
        .build()
  }

  @Bean
  fun practiceRoutes3(handler: PracticeHandler): RouterFunction<ServerResponse> = coSwaggerRouter {
    GET("/swagger-router/practice/{param}", handler::helloUniverse) { builder ->
      builder
          .operationId("operation")
          .parameter(SwaggerBuilders.pathParamBuilder().name("param"))
          .parameter(SwaggerBuilders.queryParamBuilder().name("queryParam"))
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .content(
                      SwaggerBuilders.contentBuilder()
                          .mediaType("application/json")
                          .schema(
                              SwaggerBuilders.schemaBuilder()
                                  .implementation(PracticeResponse::class.java))))
    }
  }
}
