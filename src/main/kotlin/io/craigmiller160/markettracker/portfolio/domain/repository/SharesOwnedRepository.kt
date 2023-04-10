package io.craigmiller160.markettracker.portfolio.domain.repository

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.TryEither

interface SharesOwnedRepository {
  suspend fun createAllSharesOwned(sharesOwned: List<SharesOwned>): TryEither<List<SharesOwned>>
  suspend fun findUniqueStocksInPortfolio(
      userId: TypedId<UserId>,
      portfolioId: TypedId<PortfolioId>
  ): TryEither<List<String>>

  suspend fun findUniqueStocksForUser(userId: TypedId<UserId>): TryEither<List<String>>

  suspend fun deleteAllSharesOwnedForUsers(userIds: List<TypedId<UserId>>): TryEither<Unit>
}
