package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.domain.models.toSharesOwnedResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class SharesOwnedService(
    private val sharesOwnedRepository: SharesOwnedRepository,
    private val authorizationService: AuthorizationService
) {

  suspend fun getSharesOwnedForPortfolioStock(
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedResponse>> {
    val userId = authorizationService.getUserId()
    val sharesOwned =
        if (PortfolioConstants.COMBINED_PORTFOLIO_ID == portfolioId) {
          sharesOwnedRepository.getSharesOwnedAtIntervalForUser(
              userId, stockSymbol, startDate, endDate, interval)
        } else {
          sharesOwnedRepository.getSharesOwnedAtIntervalInPortfolio(
              userId, portfolioId, stockSymbol, startDate, endDate, interval)
        }

    return sharesOwned.map { results -> results.map { it.toSharesOwnedResponse() } }
  }

  suspend fun getCurrentSharesOwnedForPortfolioStock(
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String
  ): TryEither<SharesOwnedResponse> {
    val userId = authorizationService.getUserId()
    val total =
        if (PortfolioConstants.COMBINED_PORTFOLIO_ID == portfolioId) {
          sharesOwnedRepository.getCurrentSharesOwnedForStockForUser(userId, stockSymbol)
        } else {
          sharesOwnedRepository.getCurrentSharesOwnedForStockInPortfolio(
              userId, portfolioId, stockSymbol)
        }

    return total.map { SharesOwnedResponse(date = LocalDate.now(), totalShares = it) }
  }
}
