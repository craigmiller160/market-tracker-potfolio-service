package io.craigmiller160.markettracker.portfolio.web.routes

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.web.types.SharesOwnedResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

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

private fun createResponseDates(params: SharesOwnedRouteParams): List<LocalDate> {
  val startEpochSeconds = params.startDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC)
  val endEpochSeconds = params.endDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC)
  val diffEpochSeconds = endEpochSeconds - startEpochSeconds

  val intervalCount =
      when (params.interval) {
        SharesOwnedInterval.MINUTELY -> TimeUnit.SECONDS.toMinutes(diffEpochSeconds)
        SharesOwnedInterval.DAILY -> TimeUnit.SECONDS.toDays(diffEpochSeconds)
        SharesOwnedInterval.WEEKLY -> TimeUnit.SECONDS.toDays(diffEpochSeconds) / 7
        SharesOwnedInterval.MONTHLY -> TimeUnit.SECONDS.toDays(diffEpochSeconds) / 30
      }

  TODO()
}
