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
  suspend fun findUniqueStocksInPortfolio(
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>> =
      authorizationService.getUserId().let {
        sharesOwnedRepository.findUniqueStocksInPortfolio(it, portfolioId)
      }

  suspend fun findUniqueStocksForAllPortfoliosCombined(): TryEither<List<String>> =
      authorizationService.getUserId().let { sharesOwnedRepository.findUniqueStocksForUser(it) }

  suspend fun getSharesOwnedForPortfolioStock(
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedResponse>> =
      authorizationService
          .getUserId()
          .let { userId ->
            sharesOwnedRepository.getSharesOwnedAtIntervalInPortfolio(
                userId, portfolioId, stockSymbol, startDate, endDate, interval)
          }
          .map { results -> results.map { it.toSharesOwnedResponse() } }

  suspend fun getSharesOwnedForUserStock(
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedResponse>> =
      authorizationService
          .getUserId()
          .let { userId ->
            sharesOwnedRepository.getSharesOwnedAtIntervalForUser(
                userId, stockSymbol, startDate, endDate, interval)
          }
          .map { results -> results.map { it.toSharesOwnedResponse() } }

  suspend fun getCurrentSharesOwnedForPortfolioStock(
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String
  ): TryEither<SharesOwnedResponse> {
    TODO()
  }

  suspend fun getCurrentSharesOwnedForUserStock(
      stockSymbol: String
  ): TryEither<SharesOwnedResponse> {
    TODO()
  }
}
