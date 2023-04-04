package io.craigmiller160.markettracker.portfolio.web.swagger

import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.util.UUID

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addPortfolioListResponse(): SwaggerOperationBuilder =
    response(
        SwaggerBuilders.responseBuilder()
            .responseCode("200")
            .implementationArray(PortfolioResponse::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addStockListResponse(): SwaggerOperationBuilder =
    response(
        SwaggerBuilders.responseBuilder()
            .responseCode("200")
            .implementationArray(String::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addPortfolioIdParameter(): SwaggerOperationBuilder =
    parameter(
        SwaggerBuilders.pathParamBuilder().name("portfolioId").implementation(UUID::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addStockSymbolParameter(): SwaggerOperationBuilder =
    parameter(
        SwaggerBuilders.pathParamBuilder().name("stockSymbol").implementation(String::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addSharesOwnedResponse(): SwaggerOperationBuilder =
    response(
        SwaggerBuilders.responseBuilder()
            .responseCode("200")
            .implementationArray(SharesOwnedResponse::class.java))
