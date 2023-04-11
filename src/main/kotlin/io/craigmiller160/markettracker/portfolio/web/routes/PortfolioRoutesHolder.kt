package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.web.handlers.PortfolioHandler
import io.craigmiller160.markettracker.portfolio.web.swagger.addEndDateParameter
import io.craigmiller160.markettracker.portfolio.web.swagger.addIntervalParameter
import io.craigmiller160.markettracker.portfolio.web.swagger.addPortfolioIdParameter
import io.craigmiller160.markettracker.portfolio.web.swagger.addPortfolioListResponse
import io.craigmiller160.markettracker.portfolio.web.swagger.addSharesOwnedResponse
import io.craigmiller160.markettracker.portfolio.web.swagger.addStartDateParameter
import io.craigmiller160.markettracker.portfolio.web.swagger.addStockListResponse
import io.craigmiller160.markettracker.portfolio.web.swagger.addStockSymbolParameter
import io.craigmiller160.markettracker.portfolio.web.swagger.coSwaggerRouter
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
        "/portfolios/combined/{stockSymbol}/history",
        handler::getSharesOwnedForAllPortfoliosCombinedStock) {
          it.operationId("getSharesOwnedForAllPortfoliosCombinedStock")
              .addStockSymbolParameter()
              .addStartDateParameter()
              .addEndDateParameter()
              .addIntervalParameter()
              .addSharesOwnedResponse()
        }
    GET("/portfolios/{portfolioId}", handler::getStocksForPortfolio) {
      it.operationId("getStocksForPortfolio").addPortfolioIdParameter().addStockListResponse()
    }
    GET(
        "/portfolios/{portfolioId}/{stockSymbol}/history",
        handler::getSharesOwnedForPortfolioStock) {
          it.operationId("getSharesOwnedForPortfolioStock")
              .addPortfolioIdParameter()
              .addStockSymbolParameter()
              .addStartDateParameter()
              .addEndDateParameter()
              .addIntervalParameter()
              .addSharesOwnedResponse()
        }
  }
}
