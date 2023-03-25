package io.craigmiller160.markettracker.portfolio.web.controllers

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.web.types.PracticeResponse
import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/practice2")
class PracticeController2(private val repo: PortfolioRepository) {
  @GetMapping suspend fun hello(): PracticeResponse = PracticeResponse("Hello World 2")

  @GetMapping("/experiment")
  suspend fun transactionExp() {
    val unique = UUID.randomUUID()
    val portfolio = BasePortfolio(id = TypedId(), userId = TypedId(), name = "Hello_$unique")
    repo.createPortfolio(portfolio)
  }
}
