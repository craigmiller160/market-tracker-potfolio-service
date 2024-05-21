package io.craigmiller160.markettracker.portfolio.web.types.keycloak

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponse(
    @field:JsonProperty("access_token") val accessToken: String,
    @field:JsonProperty("expires_in") val expiresIn: Int,
    @field:JsonProperty("refresh_expires_in") val refreshExpiresIn: Int,
    @field:JsonProperty("refresh_token") val refreshToken: String,
    @field:JsonProperty("token_type") val tokenType: String,
    @field:JsonProperty("not-before-policy") val notBeforePolicy: Int,
    @field:JsonProperty("session_state") val sessionState: String,
    val scope: String
)
