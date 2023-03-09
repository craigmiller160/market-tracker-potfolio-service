package io.craigmiller160.markettracker.portfolio.security

import io.craigmiller160.markettracker.portfolio.config.JwtAuthConverterConfig
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter(private val config: JwtAuthConverterConfig) :
    Converter<Jwt, AbstractAuthenticationToken> {
  override fun convert(source: Jwt): AbstractAuthenticationToken? {
    TODO("Not yet implemented")
  }
}
