package io.craigmiller160.markettracker.portfolio.domain.client

import kotlin.reflect.KClass

@JvmInline value class NullValue<T : Any>(val type: KClass<T>)

inline fun <reified T : Any> nullValue(): NullValue<T> = NullValue(T::class)
