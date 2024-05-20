package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import arrow.core.flatMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.bindToList
import io.craigmiller160.markettracker.portfolio.extensions.decodePrivateKeyPem
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class CraigMillerDownloaderService(
    private val downloaderConfig: CraigMillerDownloaderConfig,
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val downloaderServiceStandard: CraigMillerDownloaderServiceStandard,
    private val downloaderService401k: CraigMillerDownloaderService401k
) : DownloaderService {
  companion object {
    const val SPREADSHEET_SCOPE = "https://www.googleapis.com/auth/spreadsheets.readonly"
    const val TOKEN_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"
    const val GRANT_TYPE_KEY = "grant_type"
    const val ASSERTION_KEY = "assertion"
  }

  private val log = LoggerFactory.getLogger(javaClass)
  override suspend fun download(): TryEither<List<PortfolioWithHistory>> {
    log.info("Beginning download of Craig Miller portfolio data")
    val serviceAccount = readServiceAccount()

    return createJwt(serviceAccount)
        .flatMap { jwt -> getAccessToken(serviceAccount, jwt) }
        .flatMap { token ->
          listOf(downloaderServiceStandard.download(token), downloaderService401k.download(token))
              .awaitAll()
              .bindToList()
              .map { list -> list.flatten() }
        }
        .also { log.info("Completed download of Craig Miller portfolio data") }
  }

  private suspend fun getAccessToken(
      serviceAccount: GoogleApiServiceAccount,
      jwt: String
  ): TryEither<String> {
    log.debug("Authenticating for service account ${serviceAccount.clientEmail}")
    val tokenBody =
        LinkedMultiValueMap<String, String>().apply {
          add(GRANT_TYPE_KEY, TOKEN_GRANT_TYPE)
          add(ASSERTION_KEY, jwt)
        }

    return webClient
        .post()
        .uri(serviceAccount.tokenUri)
        .body(BodyInserters.fromFormData(tokenBody))
        .retrieveSuccess()
        .awaitBodyResult<GoogleApiAccessToken>()
        .map { it.accessToken }
  }

  private suspend fun readServiceAccount(): GoogleApiServiceAccount =
      withContext(Dispatchers.IO) {
        Paths.get(downloaderConfig.serviceAccountJsonPath)
            .let { Files.readString(it) }
            .let { objectMapper.readValue(it, GoogleApiServiceAccount::class.java) }
      }

  private fun createJwt(serviceAccount: GoogleApiServiceAccount): TryEither<String> =
      Either.catch {
        log.debug("Creating JWT for service account ${serviceAccount.clientEmail}")
        val nowUtc = ZonedDateTime.now(ZoneId.of("UTC"))
        val header =
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(serviceAccount.privateKeyId).build()
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
