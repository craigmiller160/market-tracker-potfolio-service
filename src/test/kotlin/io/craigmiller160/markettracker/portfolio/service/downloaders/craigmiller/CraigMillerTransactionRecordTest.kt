package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import com.github.michaelbull.result.getOrThrow
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource

class CraigMillerTransactionRecordTest {
  companion object {
    @JvmStatic
    fun rawRecordValues(): Stream<Pair<List<String>, KtResult<CraigMillerTransactionRecord>>> =
        Stream.of(
            listOf("1/1/2020", "Buy", "$1.00", "VTI", "1.1") to
                Ok(
                    CraigMillerTransactionRecord(
                        date = LocalDate.of(2020, 1, 1),
                        action = Action.BUY,
                        amount = BigDecimal("1.00"),
                        symbol = "VTI",
                        shares = BigDecimal("1.1"))),
            listOf("abc", "Buy", "$1.00", "VTI", "1.1") to
                Err(DateTimeParseException("Text 'abc' could not be parsed at index 0", "abc", 0)),
            listOf("1/1/2020", "Foo", "$1.00", "VTI", "1.1") to
                Err(IllegalArgumentException("Invalid label for action: Foo")),
            listOf("1/1/2020", "Buy", "abc", "VTI", "1.1") to
                Err(
                    NumberFormatException(
                        "Character a is neither a decimal digit number, decimal point, nor \"e\" notation exponential mark.")),
            listOf("1/1/2020", "Buy", "$1.00", "VTI", "def") to
                Err(
                    NumberFormatException(
                        "Character d is neither a decimal digit number, decimal point, nor \"e\" notation exponential mark.")))

    @JvmStatic
    fun comparatorValues():
        Stream<Triple<CraigMillerTransactionRecord, CraigMillerTransactionRecord, Int>> =
        Stream.of(
            Triple(emptyRecord(), emptyRecord().copy(date = LocalDate.of(2020, 2, 1)), -1),
            Triple(emptyRecord().copy(date = LocalDate.of(2020, 2, 1)), emptyRecord(), 1),
            Triple(
                emptyRecord().copy(date = LocalDate.of(2020, 2, 1)),
                emptyRecord().copy(symbol = "DEF"),
                -1),
            Triple(emptyRecord(), emptyRecord().copy(symbol = "DEF"), -1),
            Triple(emptyRecord(), emptyRecord(), 0),
            Triple(emptyRecord().copy(action = Action.INVESTMENT_BALANCE), emptyRecord(), 1),
            Triple(emptyRecord(), emptyRecord().copy(action = Action.INVESTMENT_BALANCE), -1))

    private fun emptyRecord(): CraigMillerTransactionRecord =
        CraigMillerTransactionRecord(
            date = LocalDate.of(2020, 1, 1),
            action = Action.BUY,
            amount = BigDecimal("0"),
            symbol = "ABC",
            shares = BigDecimal("0"))
  }
  @ParameterizedTest
  @EnumSource(Action::class)
  fun `Action fromLabel, success`(action: Action) {
    Action.fromLabel(action.label).expect { "Unable to parse label" }
  }

  @Test
  fun `CraigMillerTransactionRecord fromRaw with only 3 fields`() {
    val raw = listOf("1/1/2020", Action.BONUS.label, "$100.00")
    val result = CraigMillerTransactionRecord.fromRaw(raw).getOrThrow()
    val expected =
        CraigMillerTransactionRecord(
            date = LocalDate.of(2020, 1, 1),
            action = Action.BONUS,
            amount = BigDecimal("100.00"),
            symbol = "",
            shares = BigDecimal("0"))
    result.shouldBeEqualToComparingFields(expected)
  }

  @Test
  fun `Action fromLabel, fail`() {
    Action.fromLabel("foo").expectError {
      "${IllegalArgumentException("Invalid label for action: foo")}"
    }
  }

  @ParameterizedTest
  @MethodSource("rawRecordValues")
  fun `CraigMillerTransactionRecord fromRaw`(
      pair: Pair<List<String>, KtResult<CraigMillerTransactionRecord>>
  ) {
    val (list, expected) = pair
    val actual = CraigMillerTransactionRecord.fromRaw(list)
    when (expected) {
      is Ok -> assertEquals(expected, actual)
      is Err -> assertEquals(expected.toString(), actual.toString())
    }
  }

  @ParameterizedTest
  @MethodSource("comparatorValues")
  fun `comparator orders correctly`(
      value: Triple<CraigMillerTransactionRecord, CraigMillerTransactionRecord, Int>
  ) {
    val (record1, record2, result) = value
    CraigMillerTransactionRecord.comparator
        .compare(record1, record2)
        .shouldBeEqualComparingTo(result)
  }
}
