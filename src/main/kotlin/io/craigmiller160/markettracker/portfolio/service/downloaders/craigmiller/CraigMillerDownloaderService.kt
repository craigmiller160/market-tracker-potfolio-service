package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.models.PortfolioWithHistory
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.decodePrivateKeyPem
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDate
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
    val MAX_DATE: LocalDate = LocalDate.of(2100, 1, 1)
    val RELEVANT_ACTIONS = listOf(Action.BONUS, Action.BUY, Action.SELL)
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
        .flatMap { responsesToPortfolios(it) }
  }

  private fun responsesToPortfolios(
      responses: List<Pair<String, GoogleSpreadsheetValues>>
  ): KtResult<List<PortfolioWithHistory>> =
      responses.map { (name, response) -> transformResponse(name, response) }.combine()

  private fun transformResponse(
      portfolioName: String,
      response: GoogleSpreadsheetValues
  ): KtResult<PortfolioWithHistory> {
    val portfolioId = TypedId<PortfolioId>()
    return response.values
        .drop(1)
        .map { CraigMillerTransactionRecord.fromRaw(it) }
        .combine()
        .map { recordsToSharesOwned(portfolioId, it) }
        .map { ownershipHistory ->
          PortfolioWithHistory(
              id = portfolioId,
              name = portfolioName,
              userId = downloaderConfig.userId,
              ownershipHistory = ownershipHistory)
        }
  }

  private fun recordsToSharesOwned(
      portfolioId: TypedId<PortfolioId>,
      records: List<CraigMillerTransactionRecord>
  ): List<SharesOwned> =
      records
          .asSequence()
          .filter { RELEVANT_ACTIONS.contains(it.action) }
          .sortedBy { it.date }
          .map { OwnershipContext(mutableMapOf(), it) }
          .reduce { ctx, record ->
            val sharesOwnedList =
                ctx.sharesOwnedMap.getOrPut(record.record.symbol) { mutableListOf() }
            val lastSharesOwned = sharesOwnedList.lastOrNull()
            val lastTotalShares = lastSharesOwned?.totalShares ?: BigDecimal("0")

            val totalShares =
                when (record.record.action) {
                  Action.BUY,
                  Action.BONUS -> lastTotalShares + record.record.shares
                  Action.SELL -> lastTotalShares - record.record.shares
                  else -> BigDecimal("0")
                }
            sharesOwnedList +=
                SharesOwned(
                    id = TypedId(),
                    userId = downloaderConfig.userId,
                    portfolioId = portfolioId,
                    dateRangeStart = record.record.date,
                    dateRangeEnd = MAX_DATE,
                    symbol = record.record.symbol,
                    totalShares = totalShares)
            ctx
          }
          .sharesOwnedMap
          .values
          .flatten()

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

private data class TotalSharesHolder(val shares: BigDecimal, val date: LocalDate)

private data class OwnershipContext(
    // TODO remove the Mutability
    val sharesOwnedMap: MutableMap<String, MutableList<SharesOwned>>,
    val record: CraigMillerTransactionRecord
)
