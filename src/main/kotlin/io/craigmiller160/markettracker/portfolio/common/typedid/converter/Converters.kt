package io.craigmiller160.markettracker.portfolio.common.typedid.converter

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.toTypedId
import java.util.UUID
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToTypedIdConverter : Converter<String, TypedId<*>> {
  override fun convert(source: String): TypedId<*> = source.toTypedId<Any>()
}

@Component
class TypedIdToStringConverter : Converter<TypedId<*>, String> {
  override fun convert(source: TypedId<*>): String = source.value.toString()
}

@Component
class UuidToTypedIdConverter : Converter<UUID, TypedId<*>> {
  override fun convert(source: UUID): TypedId<*> = TypedId<Any>(source)
}

@Component
class TypedIdToUuidConverter : Converter<TypedId<*>, UUID> {
  override fun convert(source: TypedId<*>): UUID = source.value
}
