package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import io.craigmiller160.testcontainers.common.TestcontainersExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
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
constructor(private val webTestClient: WebTestClient, private val defaultUsers: DefaultUsers) {
  @Test
  fun practice() {
    webTestClient
        .get()
        .uri("/practice2")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json("""{"message": "Hello World"}""")
  }
}
