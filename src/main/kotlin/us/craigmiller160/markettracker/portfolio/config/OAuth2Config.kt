package io.craigmiller160.markettracker.portfolio.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.keycloak.oauth2.resourceserver")
data class OAuth2Config(
    val host: String,
    val realm: String,
    val clientId: String,
    val clientSecret: String
)
