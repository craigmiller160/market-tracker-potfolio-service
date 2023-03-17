package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CraigMillerTransactionRecordTest {
  @ParameterizedTest
  @ValueSource(
      strings =
          [
              "Bonus",
              "Deposit",
              "Buy",
              "Sell",
              "Withdrawal",
              "Dividend",
              "Cash Balance",
              "Investment Balance"])
  fun `Action fromLabel, success`(value: String) {
    TODO()
  }

  @Test
  fun `Action fromLabel, fail`() {
    val ex = assertThrows<IllegalArgumentException> { Action.fromLabel("foo") }
    assertEquals("Invalid label for action: foo", ex.message)
  }

  @Test
  fun `CraigMillerTransactionRecord fromRaw`() {
    TODO()
  }
}
