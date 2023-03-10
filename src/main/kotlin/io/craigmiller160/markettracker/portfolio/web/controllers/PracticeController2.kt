package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.web.types.PracticeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/practice2")
class PracticeController2 {
  @GetMapping suspend fun hello(): PracticeResponse = PracticeResponse("Hello World 2")
}
