package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.craigmiller160.markettracker.portfolio.domain.client.Row
import io.craigmiller160.markettracker.portfolio.extensions.TryEither

typealias RowMapper<T> = (Row) -> TryEither<T>
