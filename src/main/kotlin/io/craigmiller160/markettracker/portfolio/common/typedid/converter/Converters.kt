package io.craigmiller160.markettracker.portfolio.common.typedid.converter

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import java.util.UUID
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter

@Configuration
class TypedIdConverters {
  @Bean
  fun stringToTypedIdConverter(): Converter<String, TypedId<*>> = Converter { source ->
    TypedId<Any>(source)
  }
  @Bean
  fun typedIdToStringConverter(): Converter<TypedId<*>, String> = Converter { source ->
    source.value.toString()
  }
  @Bean
  fun uuidToTypedIdConverter(): Converter<UUID, TypedId<*>> = Converter { source ->
    TypedId<Any>(source)
  }
  @Bean
  fun typedIdToUuidConverter(): Converter<TypedId<*>, UUID> = Converter { source -> source.value }
}
