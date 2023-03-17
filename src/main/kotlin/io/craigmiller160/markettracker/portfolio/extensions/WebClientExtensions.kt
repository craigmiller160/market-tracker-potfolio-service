package io.craigmiller160.markettracker.portfolio.extensions

import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

suspend inline fun <reified T : Any> WebClient.ResponseSpec.awaitBodyResult(): KtResult<T> =
    ktRunCatching {
      awaitBody<T>()
    }
