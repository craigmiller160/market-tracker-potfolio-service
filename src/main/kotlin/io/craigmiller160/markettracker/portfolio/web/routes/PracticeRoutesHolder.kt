package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PracticeHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PracticeRoutesHolder {

  @Bean
  fun practiceRoutes3(handler: PracticeHandler): RouterFunction<ServerResponse> = coSwaggerRouter {
    GET("/swagger-router/practice/{param}", handler::helloUniverse) { builder ->
      builder
          .operationId("practice3")
          .parameter(SwaggerBuilders.pathParamBuilder().name("param"))
          .parameter(SwaggerBuilders.queryParamBuilder().name("queryParam"))
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementation(PracticeResponse::class.java))
    }
  }
}
