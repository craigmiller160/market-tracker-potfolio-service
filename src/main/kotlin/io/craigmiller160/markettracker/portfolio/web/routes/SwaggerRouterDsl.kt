package io.craigmiller160.markettracker.portfolio.web.routes

import kotlinx.coroutines.reactor.mono
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class RoutesFunctionDsl(private val builder: SpringdocRouteBuilder) {
  fun GET(path: String, handler: suspend (ServerRequest) -> ServerResponse) {
    val monoHandler: (ServerRequest) -> Mono<ServerResponse> = { req -> mono { handler(req) } }
    builder.GET(path, monoHandler) {}
  }
}

fun coRouterDsl(init: RoutesFunctionDsl.() -> Unit): RouterFunction<ServerResponse> {
  val builder = SpringdocRouteBuilder.route()
  val routesDsl = RoutesFunctionDsl(builder).also { it.init() }
  return builder.build()
}
