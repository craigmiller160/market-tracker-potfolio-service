package io.craigmiller160.markettracker.portfolio

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
  val accountPath =
      Paths.get(
          System.getProperty("user.home"), "Downloads", "market-tracker-380317-fdda285edeac.json")
  val objectMapper =
      jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
  val account = objectMapper.readValue(accountPath.readText(), Account::class.java)
  println(account)
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
