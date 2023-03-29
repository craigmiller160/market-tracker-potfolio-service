package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/practice2")
class PracticeController2(private val repo: PortfolioRepository) {
  @GetMapping suspend fun hello(): PracticeResponse = PracticeResponse("Hello World 2")

  @Transactional
  @GetMapping("/experiment")
  suspend fun transactionExp(@RequestParam("throwEx") throwEx: Boolean) {
    //    val unique = UUID.randomUUID()
    //    val portfolio = BasePortfolio(id = TypedId(), userId = TypedId(), name = "Hello_$unique")
    //    repo.createPortfolio(portfolio).onFailure { it.printStackTrace() }
    //    if (throwEx) {
    //      throw RuntimeException()
    //    }
    TODO()
  }
}
