package io.craigmiller160.markettracker.portfolio.domain.repository

import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.functions.KtResult

interface SharesOwnedRepository {
  suspend fun createAllSharesOwned(sharesOwned: List<SharesOwned>): KtResult<List<SharesOwned>>
}
