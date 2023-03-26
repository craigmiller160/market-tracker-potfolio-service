package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata

typealias RowMapper<T> = (Row, RowMetadata) -> KtResult<T>
