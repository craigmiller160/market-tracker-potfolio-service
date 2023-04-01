package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.DownloadHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class DownloadRoutesHolder {
  @Bean
  fun downloadRoutes(handler: DownloadHandler): RouterFunction<ServerResponse> = coSwaggerRouter {
    POST("/download", handler::download) {
      it.operationId("download").response(SwaggerBuilders.responseBuilder().responseCode("204"))
    }
  }
}
