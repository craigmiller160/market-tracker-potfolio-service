package io.craigmiller160.markettracker.portfolio.extensions

typealias KtResult<T> = com.github.michaelbull.result.Result<T, Throwable>

fun <T> ktRunCatching(block: () -> T): KtResult<T> =
    com.github.michaelbull.result.runCatching(block)
