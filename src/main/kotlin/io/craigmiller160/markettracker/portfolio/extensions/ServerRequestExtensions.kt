package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.getOrElse
import io.craigmiller160.markettracker.portfolio.web.exceptions.BadRequestException
import io.craigmiller160.markettracker.portfolio.web.exceptions.MissingParameterException
import org.springframework.web.reactive.function.server.ServerRequest

fun <T> ServerRequest.pathVariable(name: String, parser: (String) -> T): T =
    Either.catch { pathVariable(name).let(parser) }
        .getOrElse { throw BadRequestException("Error parsing path variable $name", it) }

fun <T> ServerRequest.requiredQueryParam(name: String, parser: (String) -> T): T =
    either
        .eager {
          val paramString =
              queryParam(name).leftIfEmpty().mapLeft { MissingParameterException(name) }.bind()
          Either.catch { parser(paramString) }
              .mapLeft { BadRequestException("Error parsing $name", it) }
              .bind()
        }
        .getOrElse { throw it }

fun <T> ServerRequest.optionalQueryParam(name: String, parser: (String) -> T): T? =
    queryParam(name).map { parser(it) }.orElse(null)
