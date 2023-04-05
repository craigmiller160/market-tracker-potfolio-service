package io.craigmiller160.markettracker.portfolio.web.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import io.craigmiller160.markettracker.portfolio.web.types.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class GlobalExceptionHandlerTest
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    private val defaultUsers: DefaultUsers,
    private val objectMapper: ObjectMapper
) {
  @Test
  fun `internal server error`() {
    val response =
        ErrorResponse(
            method = "GET", uri = "/throw-exception/runtime-exception", message = "", status = 500)

    webTestClient
        .get()
        .uri("/throw-exception/runtime-exception")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isEqualTo(500)
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }

  @Test
  fun `missing parameter exception`() {
    val response =
        ErrorResponse(
            method = "GET",
            uri = "/throw-exception/missing-parameter",
            message = "Bad Request: Missing required parameter: stuff",
            status = 400)

    webTestClient
        .get()
        .uri("/throw-exception/missing-parameter")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .isEqualTo(400)
        .expectBody()
        .json(objectMapper.writeValueAsString(response))
  }
}
