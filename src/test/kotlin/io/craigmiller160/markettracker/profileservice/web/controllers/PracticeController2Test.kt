package io.craigmiller160.markettracker.profileservice.web.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class PracticeController2Test(private val mockMvc: MockMvc) {
  @Test
  fun practice() {
    mockMvc.get("/practice2").andExpect {
      status { isOk() }
      content { json("""{"message": "Hello World"}""") }
    }
  }
}
