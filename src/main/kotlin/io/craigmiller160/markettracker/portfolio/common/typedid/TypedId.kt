package io.craigmiller160.markettracker.portfolio.common.typedid

import java.util.UUID

data class TypedId<T>(val value: UUID = UUID.randomUUID())
