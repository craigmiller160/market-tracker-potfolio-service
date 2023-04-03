package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PortfolioHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerBuilders
import io.craigmiller160.markettracker.portfolio.web.swagger.SwaggerOperationBuilder
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
      it.operationId("getPortfolios").addPortfolioListResponse()
    }
    GET("/portfolios/combined", handler::getStocksForAllPortfoliosCombined) {
      it.operationId("getStocksForAllPortfoliosCombined").addStockListResponse()
    }
    GET(
        "/portfolios/combined/{stockSymbol}/shares",
        handler::getSharesOwnedForAllPortfoliosCombinedStock) {
          it.operationId("getSharesOwnedForAllPortfoliosCombinedStock")
              .addStockSymbolParameter()
              .addSharesOwnedResponse()
        }
    GET("/portfolios/{portfolioId}", handler::getStocksForPortfolio) {
      it.operationId("getStocksForPortfolio").addPortfolioIdParameter().addStockListResponse()
    }
    GET(
        "/portfolios/{portfolioId}/{stockSymbol}/shares",
        handler::getSharesOwnedForPortfolioStock) {
          it.operationId("getSharesOwnedForPortfolioStock")
              .addPortfolioIdParameter()
              .addStockSymbolParameter()
              .addSharesOwnedResponse()
        }
  }

  private fun SwaggerOperationBuilder.addPortfolioListResponse(): SwaggerOperationBuilder =
      response(
          SwaggerBuilders.responseBuilder()
              .responseCode("200")
              .implementationArray(PortfolioResponse::class.java))

  private fun SwaggerOperationBuilder.addStockListResponse(): SwaggerOperationBuilder =
      response(
          SwaggerBuilders.responseBuilder()
              .responseCode("200")
              .implementationArray(String::class.java))
  private fun SwaggerOperationBuilder.addPortfolioIdParameter(): SwaggerOperationBuilder =
      parameter(
          SwaggerBuilders.pathParamBuilder().name("portfolioId").implementation(UUID::class.java))

  private fun SwaggerOperationBuilder.addStockSymbolParameter(): SwaggerOperationBuilder =
      parameter(
          SwaggerBuilders.pathParamBuilder().name("stockSymbol").implementation(String::class.java))

  private fun SwaggerOperationBuilder.addSharesOwnedResponse(): SwaggerOperationBuilder =
      response(
          SwaggerBuilders.responseBuilder()
              .responseCode("200")
              .implementationArray(SharesOwnedResponse::class.java))
}
