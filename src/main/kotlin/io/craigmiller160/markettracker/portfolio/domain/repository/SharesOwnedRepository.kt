package io.craigmiller160.markettracker.portfolio.domain.repository

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedOnDate
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import java.time.LocalDate

interface SharesOwnedRepository {
  suspend fun createAllSharesOwned(sharesOwned: List<SharesOwned>): TryEither<List<SharesOwned>>
  suspend fun findUniqueStocksInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>>

  suspend fun findUniqueStocksForUser(userId: TypedId<UserId>): TryEither<List<String>>

  suspend fun getSharesOwnedAtIntervalInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>>

  suspend fun getSharesOwnedAtIntervalForUser(
      userId: TypedId<UserId>,
      stockSymbol: String,
      startDate: LocalDate,
      endDate: LocalDate,
      interval: SharesOwnedInterval
  ): TryEither<List<SharesOwnedOnDate>>
}
