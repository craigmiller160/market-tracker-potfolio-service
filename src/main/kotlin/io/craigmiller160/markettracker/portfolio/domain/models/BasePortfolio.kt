package io.craigmiller160.markettracker.portfolio.domain.models

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId

data class BasePortfolio(
    override val id: TypedId<PortfolioId>,
    override val userId: TypedId<UserId>,
    override val name: String
) : Portfolio
