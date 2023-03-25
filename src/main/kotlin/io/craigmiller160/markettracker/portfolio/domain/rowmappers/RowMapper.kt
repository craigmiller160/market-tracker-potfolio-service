package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata

typealias RowMapper<T> = (Row, RowMetadata) -> T
