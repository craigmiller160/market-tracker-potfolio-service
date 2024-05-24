package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleApiServiceAccount(
    val type: String,
    @JsonProperty("project_id") val projectId: String,
    @JsonProperty("private_key_id") val privateKeyId: String,
    @JsonProperty("private_key") val privateKey: String,
    @JsonProperty("client_email") val clientEmail: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("auth_uri") val authUri: String,
    @JsonProperty("token_uri") val tokenUri: String,
    @JsonProperty("auth_provider_x509_cert_url") val authProviderX509CertUrl: String,
    @JsonProperty("client_x509_cert_url") val clientX509CertUrl: String
)
