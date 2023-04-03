package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PortfolioHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioNameResponse
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.util.UUID
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
    GET("/portfolios/all", handler::getStocksForAllPortfolios) {
      it.operationId("getStocksForAllPortfolios")
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementationArray(String::class.java))
    }
    GET("/portfolios/{portfolioId}", handler::getStocksForPortfolio) {
      it.operationId("getStocksForPortfolio")
          .parameter(
              SwaggerBuilders.pathParamBuilder()
                  .name("portfolioId")
                  .implementation(UUID::class.java))
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementationArray(String::class.java))
    }
    GET("/portfolios/{portfolioId}/{stockSymbol}", handler::getSharesOwnedForPortfolioStock) {
      it.operationId("getSharesOwnedForPortfolioStock")
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementationArray(SharesOwnedResponse::class.java))
    }
  }
}
