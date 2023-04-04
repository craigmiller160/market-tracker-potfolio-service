package io.craigmiller160.markettracker.portfolio.web.swagger

import io.craigmiller160.markettracker.portfolio.web.routes.RoutesHolder
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse

context(RoutesHolder)

fun SwaggerOperationBuilder.addPortfolioListResponse(): SwaggerOperationBuilder =
    response(
        SwaggerBuilders.responseBuilder()
            .responseCode("200")
            .implementationArray(PortfolioResponse::class.java))
