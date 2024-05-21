package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.config.MarketTrackerApiConfig
import io.craigmiller160.markettracker.portfolio.config.OAuth2Config
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import io.craigmiller160.markettracker.portfolio.web.types.keycloak.TokenResponse
import io.craigmiller160.markettracker.portfolio.web.types.tradier.TradierHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class TradierService(
    private val webClient: WebClient,
    private val marketTrackerApiConfig: MarketTrackerApiConfig,
    private val oauth2Config: OAuth2Config
) {
  companion object {
    val TRADIER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  }

  suspend fun getTradierHistory(symbols: List<String>): TryEither<Map<String, TradierHistory>> =
      coroutineScope {
        getAccessToken().flatMap { token ->
          symbols
              .map { symbol ->
                async { downloadTradierHistory(symbol, token).map { symbol to it } }
              }
              .awaitAll()
              .bindToList()
              .map { it.toMap() }
        }
      }

  private suspend fun downloadTradierHistory(
      symbol: String,
      token: String
  ): TryEither<TradierHistory> {
    val today = LocalDate.now().format(TRADIER_DATE_FORMAT)
    return webClient
        .get()
        .uri(
            "${marketTrackerApiConfig.host}/tradier/markets/history?symbol=${symbol}&start=2015-01-01&end=$today&interval=monthly")
        .header("Authorization", "Bearer $token")
        .retrieveSuccess()
        .awaitBodyResult<TradierHistory>()
  }

  private suspend fun getAccessToken(): TryEither<String> {
    val body =
        LinkedMultiValueMap<String, String>().apply {
          add("grant_type", "client_credentials")
          add("client_id", oauth2Config.clientId)
          add("client_secret", oauth2Config.clientSecret)
        }

    return webClient
        .post()
        .uri("${oauth2Config.host}/realms/${oauth2Config.realm}/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(body))
        .retrieveSuccess()
        .awaitBodyResult<TokenResponse>()
        .map { it.accessToken }
  }
}
