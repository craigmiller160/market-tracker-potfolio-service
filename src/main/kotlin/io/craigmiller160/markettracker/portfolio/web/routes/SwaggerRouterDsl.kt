package io.craigmiller160.markettracker.portfolio.web.routes

import kotlinx.coroutines.reactor.mono
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

typealias SwaggerOperationBuilder = org.springdoc.core.fn.builders.operation.Builder

fun suspendToMonoHandler(
    handler: suspend (ServerRequest) -> ServerResponse
): (ServerRequest) -> Mono<ServerResponse> = { request -> mono { handler(request) } }

class SwaggerDsl(private val builder: SwaggerOperationBuilder) {}

class SwaggerRouterDsl(private val builder: SpringdocRouteBuilder) {
  fun GET(
      path: String,
      handler: suspend (ServerRequest) -> ServerResponse,
      swagger: SwaggerDsl.() -> Unit = {}
  ) {
    builder.GET(path, suspendToMonoHandler(handler)) { builder ->
      val swaggerDsl = SwaggerDsl(builder)
      swaggerDsl.swagger()
    }
  }
}

fun coSwaggerRouter(init: SwaggerRouterDsl.() -> Unit): RouterFunction<ServerResponse> {
  val builder = SpringdocRouteBuilder.route()
  val routesDsl = SwaggerRouterDsl(builder).also { it.init() }
  return builder.build()
}
