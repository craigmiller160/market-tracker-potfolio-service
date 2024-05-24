package io.craigmiller160.markettracker.portfolio.extensions

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.time.LocalDate
import org.junit.jupiter.api.Test

class LocalDateExtensionsTest {
  @Test
  fun test_isBetween() {
    val date1 = LocalDate.of(2020, 1, 1)
    val date2 = LocalDate.of(2020, 1, 2)
    val date3 = LocalDate.of(2020, 1, 3)

    date2.isBetween(date1, date3).shouldBeTrue()
    date2.isBetween(date2, date3).shouldBeTrue()
    date2.isBetween(date1, date2).shouldBeTrue()
    date1.isBetween(date2, date3).shouldBeFalse()
    date3.isBetween(date1, date2).shouldBeFalse()
  }

  @Test
  fun test_isAfterOrEqual() {
    val date1 = LocalDate.of(2020, 1, 1)
    val date2 = LocalDate.of(2020, 1, 2)

    date2.isAfterOrEqual(date1).shouldBeTrue()
    date2.isAfterOrEqual(date2).shouldBeTrue()
    date1.isAfterOrEqual(date2).shouldBeFalse()
  }

  @Test
  fun test_isBeforeOrEqual() {
    val date1 = LocalDate.of(2020, 1, 1)
    val date2 = LocalDate.of(2020, 1, 2)

    date1.isBeforeOrEqual(date2).shouldBeTrue()
    date1.isBeforeOrEqual(date1).shouldBeTrue()
    date2.isBeforeOrEqual(date1).shouldBeFalse()
  }
}
