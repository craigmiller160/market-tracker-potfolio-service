package io.craigmiller160.markettracker.portfolio.web.routes

import io.swagger.v3.oas.annotations.enums.ParameterIn
import java.util.function.Consumer
import kotlinx.coroutines.reactor.mono
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

typealias SwaggerOperationBuilder = org.springdoc.core.fn.builders.operation.Builder

typealias SwaggerParameterBuilder = org.springdoc.core.fn.builders.parameter.Builder

typealias SwaggerResponseBuilder = org.springdoc.core.fn.builders.apiresponse.Builder

typealias SwaggerContentBuilder = org.springdoc.core.fn.builders.content.Builder

typealias SwaggerExampleBuilder = org.springdoc.core.fn.builders.exampleobject.Builder

typealias SwaggerSchemaBuilder = org.springdoc.core.fn.builders.schema.Builder

typealias SwaggerArraySchemaBuilder = org.springdoc.core.fn.builders.arrayschema.Builder

object SwaggerBuilders {
  fun pathParamBuilder(): SwaggerParameterBuilder =
      SwaggerParameterBuilder.parameterBuilder().`in`(ParameterIn.PATH)
  fun queryParamBuilder(): SwaggerParameterBuilder =
      SwaggerParameterBuilder.parameterBuilder().`in`(ParameterIn.QUERY)
  fun responseBuilder(): SwaggerResponseBuilder = SwaggerResponseBuilder.responseBuilder()
  fun contentBuilder(): SwaggerContentBuilder = SwaggerContentBuilder.contentBuilder()
  fun exampleBuilder(): SwaggerExampleBuilder = SwaggerExampleBuilder.exampleOjectBuilder()
  fun schemaBuilder(): SwaggerSchemaBuilder = SwaggerSchemaBuilder.schemaBuilder()
  fun arraySchemaBuilder(): SwaggerArraySchemaBuilder =
      SwaggerArraySchemaBuilder.arraySchemaBuilder()
}

fun suspendToMonoHandler(
    handler: suspend (ServerRequest) -> ServerResponse
): (ServerRequest) -> Mono<ServerResponse> = { request -> mono { handler(request) } }

class SwaggerRouterDsl(private val builder: SpringdocRouteBuilder) {
  fun GET(
      path: String,
      handler: suspend (ServerRequest) -> ServerResponse,
      swagger: Consumer<SwaggerOperationBuilder> = Consumer {}
  ) {
    builder.GET(path, suspendToMonoHandler(handler), swagger)
  }
}

fun coSwaggerRouter(init: SwaggerRouterDsl.() -> Unit): RouterFunction<ServerResponse> {
  val builder = SpringdocRouteBuilder.route()
  val routesDsl = SwaggerRouterDsl(builder).also { it.init() }
  return builder.build()
}
