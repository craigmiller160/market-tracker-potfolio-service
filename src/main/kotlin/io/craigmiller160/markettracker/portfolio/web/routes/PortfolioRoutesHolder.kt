package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PortfolioHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
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
                  .implementationArray(PortfolioResponse::class.java))
    }
    GET("/portfolios/combined", handler::getStocksForAllPortfoliosCombined) {
      it.operationId("getStocksForAllPortfoliosCombined")
          .response(
              SwaggerBuilders.responseBuilder()
                  .responseCode("200")
                  .implementationArray(String::class.java))
    }
    GET(
        "/portfolios/combined/{stockSymbol}/shares",
        handler::getSharesOwnedForAllPortfoliosCombinedStock) {
          it.operationId("getSharesOwnedForAllPortfoliosCombinedStock")
              .parameter(
                  SwaggerBuilders.pathParamBuilder()
                      .name("stockSymbol")
                      .implementation(String::class.java))
              .response(
                  SwaggerBuilders.responseBuilder()
                      .responseCode("200")
                      .implementationArray(SharesOwnedResponse::class.java))
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
    GET(
        "/portfolios/{portfolioId}/{stockSymbol}/shares",
        handler::getSharesOwnedForPortfolioStock) {
          it.operationId("getSharesOwnedForPortfolioStock")
              .parameter(
                  SwaggerBuilders.pathParamBuilder()
                      .name("portfolioId")
                      .implementation(UUID::class.java))
              .parameter(
                  SwaggerBuilders.pathParamBuilder()
                      .name("stockSymbol")
                      .implementation(String::class.java))
              .response(
                  SwaggerBuilders.responseBuilder()
                      .responseCode("200")
                      .implementationArray(SharesOwnedResponse::class.java))
        }
  }
}
