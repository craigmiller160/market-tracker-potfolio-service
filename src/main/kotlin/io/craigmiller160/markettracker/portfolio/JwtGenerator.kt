package io.craigmiller160.markettracker.portfolio

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nimbusds.jwt.JWTClaimsSet
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.io.path.readText

fun main() {
  val accountPath =
      Paths.get(
          System.getProperty("user.home"), "Downloads", "market-tracker-380317-fdda285edeac.json")
  val objectMapper =
      jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
  val account = objectMapper.readValue(accountPath.readText(), Account::class.java)
  println(account)

  val nowUtc = ZonedDateTime.now(ZoneId.of("UTC"))

  val claims =
      mapOf(
          "sub" to account.clientEmail,
          "iss" to account.clientEmail,
          "scope" to "https://www.googleapis.com/auth/spreadsheets.readonly",
          "aud" to account.tokenUri,
          "iat" to nowUtc.toEpochSecond(),
          "exp" to nowUtc.plusMinutes(30).toEpochSecond())

  val claimsSet = JWTClaimsSet.parse(claims)
}

data class Account(
    val type: String,
    val projectId: String,
    val privateKeyId: String,
    val privateKey: String,
    val clientEmail: String,
    val clientId: String,
    val authUri: String,
    val tokenUri: String,
    val authProviderX509CertUrl: String,
    val clientX509CertUrl: String
)
