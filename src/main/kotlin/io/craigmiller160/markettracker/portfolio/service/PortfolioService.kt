package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioNameResponse
import org.springframework.stereotype.Service

@Service
class PortfolioService {
  suspend fun getPortfolioNames(): TryEither<List<PortfolioNameResponse>> {
    TODO()
  }
}
