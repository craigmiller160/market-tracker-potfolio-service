package io.craigmiller160.markettracker.portfolio.functions

import com.github.michaelbull.result.flatMap

typealias KtResult<T> = com.github.michaelbull.result.Result<T, Throwable>

inline fun <T> ktRunCatching(block: () -> T): KtResult<T> =
    com.github.michaelbull.result.runCatching(block)

suspend fun <T, R> KtResult<T>.coFlatMap(block: suspend (T) -> KtResult<R>): KtResult<R> = flatMap {
  block(it)
}
