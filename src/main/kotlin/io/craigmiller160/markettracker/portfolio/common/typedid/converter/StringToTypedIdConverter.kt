package io.craigmiller160.markettracker.portfolio.common.typedid.converter

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import java.util.UUID
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToTypedIdConverter : Converter<String, TypedId<UUID>> {
  override fun convert(source: String): TypedId<UUID> = TypedId(source)
}
