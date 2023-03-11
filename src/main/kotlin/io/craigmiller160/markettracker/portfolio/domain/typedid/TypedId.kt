package io.craigmiller160.markettracker.portfolio.domain.typedid

import java.util.UUID

data class TypedId<T>(val value: UUID = UUID.randomUUID())
