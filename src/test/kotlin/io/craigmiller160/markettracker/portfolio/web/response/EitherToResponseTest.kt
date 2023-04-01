package io.craigmiller160.markettracker.portfolio.web.response

import arrow.core.Either
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class EitherToResponseTest {
  @Test
  fun `converts a Right into a 200 response`() {
    val response = runBlocking { Either.Right("Hello World").toResponse() }
    response.statusCode().value().shouldBe(200)
    TODO("Figure out how to get the body")
  }

  @Test
  fun `throws exception for Left`() {
    runBlocking {
      shouldThrow<RuntimeException> { Either.Left(RuntimeException("Dying")).toResponse() }
    }
  }

  @Test
  fun `converts a Right into a 202 response with custom headers`() {
    val response = runBlocking {
      Either.Right("Hello World").toResponse(202) { header("ABC", "DEF") }
    }
    response.statusCode().value().shouldBe(202)
    response.headers()["ABC"].shouldNotBeNull().shouldHaveSize(1).first().shouldBe("DEF")
    TODO("Figure out how to get the body")
  }
}
