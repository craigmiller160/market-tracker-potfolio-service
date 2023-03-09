package io.craigmiller160.markettracker.profileservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/practice")
class PracticeController {
  @GetMapping fun hello(): String = "Hello World"
}
