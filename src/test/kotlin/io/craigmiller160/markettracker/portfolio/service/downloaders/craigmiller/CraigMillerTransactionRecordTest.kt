package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CraigMillerTransactionRecordTest {
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

  @Test
  fun `CraigMillerTransactionRecord fromRaw`() {
    TODO()
  }
}
