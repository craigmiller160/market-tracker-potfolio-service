package io.craigmiller160.markettracker.portfolio.web.swagger

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.time.LocalDate
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

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addNoContentResponse(): SwaggerOperationBuilder =
    response(SwaggerBuilders.responseBuilder().responseCode("204"))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addStartDateParameter(): SwaggerOperationBuilder =
    parameter(
        SwaggerBuilders.queryParamBuilder().name("startDate").implementation(LocalDate::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addEndDateParameter(): SwaggerOperationBuilder =
    parameter(
        SwaggerBuilders.queryParamBuilder().name("endDate").implementation(LocalDate::class.java))

context(SwaggerRouterDsl)

fun SwaggerOperationBuilder.addIntervalParameter(): SwaggerOperationBuilder =
    parameter(
        SwaggerBuilders.queryParamBuilder()
            .name("interval")
            .implementation(SharesOwnedInterval::class.java))
