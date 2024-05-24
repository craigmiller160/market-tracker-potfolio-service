package io.craigmiller160.markettracker.portfolio.web.types.keycloak

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponse(
    @field:JsonProperty("access_token") val accessToken: String,
    @field:JsonProperty("expires_in") val expiresIn: Int,
    @field:JsonProperty("token_type") val tokenType: String
)
