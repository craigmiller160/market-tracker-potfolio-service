package io.craigmiller160.markettracker.portfolio.domain.client

fun paramsBuilder(init: ParamsBuilder.() -> Unit): Map<String, Any> =
    ParamsBuilder().apply(init).params

class ParamsBuilder {
  val params: MutableMap<String, Any> = mutableMapOf()

  inline fun <reified T : Any> nullValue(): NullValue<T> = NullValue(T::class)

  inline operator fun <reified T : Any> plus(pair: Pair<String, T?>) {
    val (key, value) = pair
    when (value) {
      null -> params += key to nullValue<T>()
      else -> params += key to value
    }
  }
}
