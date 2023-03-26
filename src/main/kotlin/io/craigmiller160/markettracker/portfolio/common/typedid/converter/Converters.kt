package io.craigmiller160.markettracker.portfolio.common.typedid.converter

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import java.util.UUID
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

// TODO https://stackoverflow.com/questions/64351344/r2dbc-custom-converter

@Component
class StringToTypedIdConverter : Converter<String, TypedId<*>> {
  override fun convert(source: String): TypedId<*> = TypedId<Any>(source)
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
