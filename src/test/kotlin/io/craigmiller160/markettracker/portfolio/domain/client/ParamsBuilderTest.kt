package io.craigmiller160.markettracker.portfolio.domain.client

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.UUID
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ParamsBuilderTest {
  companion object {
    private val GUID = UUID.randomUUID()
    @JvmStatic
    fun paramsBuilderArgs(): Stream<ParamsBuilderArgs> =
        Stream.of(
            ParamsBuilderArgs(
                {
                  it + ("hello" to GUID)
                  it + ("world" to 123)
                },
                mapOf("hello" to GUID, "world" to 123)),
            ParamsBuilderArgs(
                {
                  it + ("hello" to GUID)
                  it + ("world" to null as Int?)
                },
                mapOf("hello" to GUID, "world" to NullValue(Int::class))),
            ParamsBuilderArgs(
                {
                  it + ("hello" to GUID)
                  it + ("world" to null)
                },
                mapOf("hello" to GUID, "world" to NullValue(Any::class))),
            ParamsBuilderArgs(
                {
                  it + ("hello" to GUID)
                  it + ("world" to it.nullValue<BigDecimal>())
                },
                mapOf("hello" to GUID, "world" to NullValue(BigDecimal::class))))
    @JvmStatic
    fun batchParamsBuilderArgs(): Stream<BatchParamsBuilderArgs> =
        Stream.of(
            BatchParamsBuilderArgs(
                {
                  it + GUID
                  it + 123
                },
                listOf(GUID, 123)),
            BatchParamsBuilderArgs(
                {
                  it + GUID
                  it + (null as Int?)
                },
                listOf(GUID, NullValue(Int::class))),
            BatchParamsBuilderArgs(
                {
                  it + GUID
                  it + null
                },
                listOf(GUID, NullValue(Any::class))),
            BatchParamsBuilderArgs(
                {
                  it + GUID
                  it + it.nullValue<BigDecimal>()
                },
                listOf(GUID, NullValue(BigDecimal::class))))
  }

  @ParameterizedTest
  @MethodSource("paramsBuilderArgs")
  fun `params builder`(args: ParamsBuilderArgs) {
    val actual = paramsBuilder { args.input(this) }
    actual.shouldBe(args.expected)
  }

  @ParameterizedTest
  @MethodSource("batchParamsBuilderArgs")
  fun `batch params builder`(args: BatchParamsBuilderArgs) {
    val actual = batchParamsBuilder { args.input(this) }
    actual.shouldBe(args.expected)
  }
}

data class ParamsBuilderArgs(val input: (ParamsBuilder) -> Unit, val expected: Map<String, Any>)

data class BatchParamsBuilderArgs(val input: (BatchParamsBuilder) -> Unit, val expected: List<Any>)
