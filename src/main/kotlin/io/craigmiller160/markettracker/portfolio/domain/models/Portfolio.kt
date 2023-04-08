package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.web.types.PortfolioResponse

interface Portfolio {
  val id: TypedId<PortfolioId>
  val userId: TypedId<UserId>
  val name: String
}

fun Portfolio.toPortfolioResponse(): PortfolioResponse =
    PortfolioResponse(id = this.id, name = this.name)
