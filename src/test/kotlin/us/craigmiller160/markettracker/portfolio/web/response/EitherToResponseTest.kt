package io.craigmiller160.markettracker.portfolio.web.response

import arrow.core.Either
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.result.view.ViewResolver

class EitherToResponseTest {
  @Test
  fun `converts a Right into a 200 response`() {
    val response = runBlocking { Either.Right("Hello World").toResponse() }
    response.statusCode().value().shouldBe(200)

    runBlocking { response.getBody() }.shouldBe("Hello World")
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

    runBlocking { response.getBody() }.shouldBe("Hello World")
  }

  private suspend fun ServerResponse.getBody(): String {
    val mockExchange =
        MockServerHttpRequest.get("http://thisdoenstmatter.com").build().let {
          MockServerWebExchange.from(it)
        }
    val mockContext =
        object : ServerResponse.Context {
          override fun messageWriters(): MutableList<HttpMessageWriter<*>> =
              HandlerStrategies.withDefaults().messageWriters()

          override fun viewResolvers(): MutableList<ViewResolver> = mutableListOf()
        }

    writeTo(mockExchange, mockContext).awaitFirstOrNull()
    return mockExchange.response.bodyAsString.awaitSingle()
  }
}
