package io.craigmiller160.markettracker.portfolio.common.typedid.converter

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToTypedIdConverter : Converter<String, TypedId<*>> {
  override fun convert(source: String): TypedId<*> = TypedId<Any>(source)
}
