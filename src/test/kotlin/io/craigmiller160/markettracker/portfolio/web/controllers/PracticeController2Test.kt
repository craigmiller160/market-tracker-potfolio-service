package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.testcontainers.common.TestcontainersExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.get

@SpringBootTest
@ExtendWith(TestcontainersExtension::class, SpringExtension::class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class PracticeController2Test
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    @Value("\${spring.r2dbc.url}") private val prop: String
) {
  @Test
  fun practice() {
    println(prop) // TODO delete this
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
