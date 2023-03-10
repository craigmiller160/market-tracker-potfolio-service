package io.craigmiller160.markettracker.portfolio.security

import io.craigmiller160.markettracker.portfolio.config.JwtAuthConverterConfig
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthConverter(private val config: JwtAuthConverterConfig) :
    Converter<Jwt, Mono<AbstractAuthenticationToken>> {
  override fun convert(jwt: Jwt): Mono<AbstractAuthenticationToken> =
      JwtAuthenticationToken(jwt, getRoles(jwt), getPrincipalName(jwt)).let { Mono.just(it) }

  private fun getPrincipalName(jwt: Jwt): String =
      jwt.getClaim<String>(config.principalAttribute ?: JwtClaimNames.SUB)

  private fun getRoles(jwt: Jwt): Collection<out GrantedAuthority> =
      jwt.getClaim<Map<String, Map<String, Collection<String>>>>("resource_access")
          .entries
          .first { (key) -> key == config.resourceId }
          .value["roles"]
          ?.map { role -> SimpleGrantedAuthority("ROLE_$role") }
          ?.toSet()
          ?: setOf()
}
