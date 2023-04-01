package io.craigmiller160.markettracker.portfolio.testcontainer

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/throw-exception")
class ExceptionThrowingController {
  @GetMapping("/runtime-exception")
  fun runtimeException() {
    throw RuntimeException("Dying")
  }
}
