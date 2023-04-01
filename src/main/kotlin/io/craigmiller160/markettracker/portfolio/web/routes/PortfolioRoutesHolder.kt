package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PortfolioHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioNameResponse
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class PortfolioRoutesHolder {
  @Bean
  fun portfolioRoutes(handler: PortfolioHandler): RouterFunction<ServerResponse> = coSwaggerRouter {
    GET("/portfolios", handler::getPortfolios) {
      it.operationId("getPortfolios")
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementationArray(PortfolioNameResponse::class.java))
    }
  }
}
