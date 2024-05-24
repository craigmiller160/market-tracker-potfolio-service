package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.springkeycloakoauth2resourceserver.security.KeycloakOAuth2ResourceServerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig(private val keycloakProvider: KeycloakOAuth2ResourceServerProvider) {

  @Bean
  fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
      http
          .csrf { it.disable() }
          .oauth2ResourceServer(keycloakProvider.provideWebFlux())
          .authorizeExchange {
            it.pathMatchers(
                    "/actuator/health",
                    "/v3/api-docs",
                    "/v3/api-docs/*",
                    "/swagger-ui.html",
                    "/webjars/**")
                .permitAll()
                .pathMatchers("/**")
                .hasRole("access")
          }
          .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
          .build()
}
