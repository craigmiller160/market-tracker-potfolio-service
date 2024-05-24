package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleApiAccessToken(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("token_type") val tokenType: String
)
