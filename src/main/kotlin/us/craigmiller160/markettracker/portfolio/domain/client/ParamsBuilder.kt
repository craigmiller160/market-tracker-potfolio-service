package io.craigmiller160.markettracker.portfolio.domain.client

fun paramsBuilder(init: ParamsBuilder.() -> Unit): Map<String, Any> =
    ParamsBuilder().apply(init).params

fun batchParamsBuilder(init: BatchParamsBuilder.() -> Unit): List<Any> =
    BatchParamsBuilder().apply(init).params

sealed class ParamsBuilderSupport {
  inline fun <reified T : Any> nullValue(): NullValue<T> = NullValue(T::class)

  inline fun <reified T : Any> handleNullableValue(value: T?): Any =
      when (value) {
        null -> nullValue<T>()
        else -> value
      }
}

class ParamsBuilder : ParamsBuilderSupport() {
  val params: MutableMap<String, Any> = mutableMapOf()

  inline operator fun <reified T : Any> plus(pair: Pair<String, T?>) {
    val (key, value) = pair
    params += key to handleNullableValue(value)
  }
}

class BatchParamsBuilder : ParamsBuilderSupport() {
  var params: MutableList<Any> = mutableListOf()

  inline operator fun <reified T : Any> plus(value: T?) {
    params += handleNullableValue(value)
  }
}
