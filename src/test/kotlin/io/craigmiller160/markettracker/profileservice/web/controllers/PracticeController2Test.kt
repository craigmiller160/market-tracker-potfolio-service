package io.craigmiller160.markettracker.profileservice.web.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class PracticeController2Test(private val mockMvc: MockMvc) {
  @Test
  fun practice() {
    TODO()
  }
}
