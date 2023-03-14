package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.KtResult
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.decodePrivateKeyPem
import io.craigmiller160.markettracker.portfolio.extensions.ktRunCatching
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec

@Service
class CraigMillerDownloaderService(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) : DownloaderService {
  companion object {
    const val SPREADSHEET_SCOPE = "https://www.googleapis.com/auth/spreadsheets.readonly"
    const val TOKEN_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"
    const val GRANT_TYPE_KEY = "grant_type"
    const val ASSERTION_KEY = "assertion"
  }

  private val log = LoggerFactory.getLogger(javaClass)
  override suspend fun download(): KtResult<List<PortfolioWithHistory>> {
    log.info("Beginning download of Craig Miller portfolio data")
    val serviceAccount = readServiceAccount()
    log.debug("Authenticating for service account ${serviceAccount.clientEmail}")

    return createJwt(serviceAccount)
        .flatMap { jwt -> getAccessToken(serviceAccount, jwt) }
        .flatMap { token ->
          downloaderConfig.portfolioSpreadsheets
              .map { config -> config.name to getTransactionDataFromSpreadsheet(config, token) }
              .map { (name, response) ->
                response.awaitBodyResult<GoogleSpreadsheetValues>().map { name to it }
              }
              .combine()
        }
        .map { results -> results.map { (name, response) -> transformResponse(name, response) } }
        .onFailure { it.printStackTrace() }
        .onSuccess { println(it) }
  }

  private fun transformResponse(
      portfolioName: String,
      response: GoogleSpreadsheetValues
  ): PortfolioWithHistory {
    val ownershipHistory = response.values.drop(1).map { row -> TODO() }
    return PortfolioWithHistory(
        id = TypedId(),
        name = portfolioName,
        userId = downloaderConfig.userId,
        ownershipHistory = ownershipHistory)
  }

  private fun getTransactionDataFromSpreadsheet(
      config: PortfolioConfig,
      accessToken: String
  ): ResponseSpec =
      webClient
          .get()
          .uri(
              "${downloaderConfig.googleSheetsApiBaseUrl}/spreadsheets/${config.sheetId}/values/${config.valuesRange}")
          .header("Authorization", "Bearer $accessToken")
          .retrieve()

  private suspend fun getAccessToken(
      serviceAccount: GoogleApiServiceAccount,
      jwt: String
  ): KtResult<String> {
    val tokenBody =
        LinkedMultiValueMap<String, String>().apply {
          add(GRANT_TYPE_KEY, TOKEN_GRANT_TYPE)
          add(ASSERTION_KEY, jwt)
        }

    return webClient
        .post()
        .uri(serviceAccount.tokenUri)
        .body(BodyInserters.fromFormData(tokenBody))
        .retrieve()
        .awaitBodyResult<GoogleApiAccessToken>()
        .map { it.accessToken }
  }

  private suspend fun readServiceAccount(): GoogleApiServiceAccount =
      withContext(Dispatchers.IO) {
        Paths.get(downloaderConfig.serviceAccountJsonPath)
            .let { Files.readString(it) }
            .let { objectMapper.readValue(it, GoogleApiServiceAccount::class.java) }
      }

  private fun createJwt(serviceAccount: GoogleApiServiceAccount): KtResult<String> = ktRunCatching {
    val nowUtc = ZonedDateTime.now(ZoneId.of("UTC"))
    val header = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(serviceAccount.privateKeyId).build()
    val claims =
        mapOf(
                "sub" to serviceAccount.clientEmail,
                "iss" to serviceAccount.clientEmail,
                "scope" to SPREADSHEET_SCOPE,
                "aud" to serviceAccount.tokenUri,
                "iat" to nowUtc.toEpochSecond(),
                "exp" to nowUtc.plusMinutes(30).toEpochSecond())
            .let { JWTClaimsSet.parse(it) }

    val signer =
        serviceAccount.privateKey
            .decodePrivateKeyPem()
            .let { PKCS8EncodedKeySpec(it) }
            .let { KeyFactory.getInstance("RSA").generatePrivate(it) }
            .let { RSASSASigner(it) }

    SignedJWT(header, claims).also { it.sign(signer) }.serialize()
  }
}
