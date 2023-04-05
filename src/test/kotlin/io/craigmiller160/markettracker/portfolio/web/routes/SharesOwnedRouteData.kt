package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.time.LocalDate

data class SharesOwnedRouteParams(
    val stockSymbol: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val interval: SharesOwnedInterval,
    val userId: TypedId<UserId>,
    val portfolioId: TypedId<PortfolioId>? = null
)

fun createSharesOwnedRouteData(
    data: PortfolioRouteData,
    params: SharesOwnedRouteParams
): List<SharesOwnedResponse> {
  val sharesOwned =
      data.sharesOwned
          .asSequence()
          .filter { record -> record.userId == params.userId }
          .filter { record ->
            params.portfolioId == null || params.portfolioId == record.portfolioId
          }
          .filter { record -> record.symbol == params.stockSymbol }
          .filter { record ->
            (record.dateRangeStart >= params.startDate &&
                record.dateRangeStart <= params.endDate) || record.dateRangeEnd >= params.startDate
          }
          .sortedBy { it.dateRangeStart }
          .toList()

  TODO()
}
