package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.markettracker.portfolio.security.JwtAuthConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig(private val jwtAuthConverter: JwtAuthConverter) {

  @Bean
  fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
      http
          .csrf()
          .disable()
          .oauth2ResourceServer { it.jwt().jwtAuthenticationConverter(jwtAuthConverter) }
          .authorizeExchange {
            it.pathMatchers(
                    "/actuator/health",
                    "/v3/api-docs",
                    "/v3/api-docs/*",
                    "/swagger-ui.html",
                    "/webjars/**")
                .permitAll()
                .pathMatchers("/**")
                .permitAll()
            //                .hasRole("access") // TODO restore security
          }
          .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
          .build()
}
