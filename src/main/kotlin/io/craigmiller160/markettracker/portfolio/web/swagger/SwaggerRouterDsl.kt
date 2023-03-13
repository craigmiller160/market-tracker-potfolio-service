package io.craigmiller160.markettracker.portfolio.web.swagger

import java.util.function.Consumer
import kotlinx.coroutines.reactor.mono
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

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
  SwaggerRouterDsl(builder).init()
  return builder.build()
}
