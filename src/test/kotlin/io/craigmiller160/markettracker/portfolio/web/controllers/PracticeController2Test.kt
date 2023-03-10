package io.craigmiller160.markettracker.portfolio.web.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.get

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class PracticeController2Test @Autowired constructor(private val webTestClient: WebTestClient) {
  @Test
  fun practice() {
    webTestClient
        .get()
        .uri("/practice2")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json("""{"message": "Hello World"}""")
  }
}
