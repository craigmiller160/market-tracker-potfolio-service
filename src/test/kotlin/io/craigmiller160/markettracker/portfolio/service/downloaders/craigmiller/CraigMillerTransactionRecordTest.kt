package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import java.math.BigDecimal
import java.time.LocalDate
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
                        shares = BigDecimal("1.1"))))
  }
  @ParameterizedTest
  @EnumSource(Action::class)
  fun `Action fromLabel, success`(action: Action) {
    Action.fromLabel(action.label).expect { "Unable to parse label" }
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
    assertEquals(expected, CraigMillerTransactionRecord.fromRaw(list))
  }
}
