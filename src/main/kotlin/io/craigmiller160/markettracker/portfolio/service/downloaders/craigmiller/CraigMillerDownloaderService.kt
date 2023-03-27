package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import com.fasterxml.jackson.databind.ObjectMapper
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
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.awaitBodyResult
import io.craigmiller160.markettracker.portfolio.extensions.decodePrivateKeyPem
import io.craigmiller160.markettracker.portfolio.extensions.retrieveSuccess
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloaderService
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
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
  override suspend fun download(): TryEither<List<PortfolioWithHistory>> {
    log.info("Beginning download of Craig Miller portfolio data")
    val serviceAccount = readServiceAccount()

    return createJwt(serviceAccount)
        .flatMap { jwt -> getAccessToken(serviceAccount, jwt) }
        .flatMap { token ->
          downloaderConfig.portfolioSpreadsheets
              .map { config -> config.name to getTransactionDataFromSpreadsheet(config, token) }
              .map { (name, response) ->
                response.awaitBodyResult<GoogleSpreadsheetValues>().map { name to it }
              }
              .sequence()
        }
        .flatMap { responsesToPortfolios(it) }
  }

  private fun responsesToPortfolios(
      responses: List<Pair<String, GoogleSpreadsheetValues>>
  ): TryEither<List<PortfolioWithHistory>> {
    log.debug("Parsing and formatting google spreadsheet responses")
    return responses.map { (name, response) -> transformResponse(name, response) }.sequence()
  }

  private fun transformResponse(
      portfolioName: String,
      response: GoogleSpreadsheetValues
  ): TryEither<PortfolioWithHistory> {
    val portfolioId = TypedId<PortfolioId>()
    return response.values
        .drop(1)
        .map { CraigMillerTransactionRecord.fromRaw(it) }
        .sequence()
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
          .sortedWith(CraigMillerTransactionRecord.comparator)
          .map(initialRecord(portfolioId))
          .reduce(reduceOwnershipContext(portfolioId))
          .sharesOwnedMap
          .values
          .flatten()

  private fun initialRecord(
      portfolioId: TypedId<PortfolioId>
  ): (CraigMillerTransactionRecord) -> OwnershipContext = { record ->
    val sharesOwned =
        SharesOwned(
            id = TypedId(),
            userId = downloaderConfig.userId,
            portfolioId = portfolioId,
            dateRangeStart = record.date,
            dateRangeEnd = CraigMillerDownloaderService.MAX_DATE,
            symbol = record.symbol,
            totalShares = record.shares)
    OwnershipContext(
        sharesOwnedMap = persistentMapOf(record.symbol to persistentListOf(sharesOwned)),
        record = record)
  }

  private fun reduceOwnershipContext(
      portfolioId: TypedId<PortfolioId>
  ): (OwnershipContext, OwnershipContext) -> OwnershipContext {
    return { accumulator, record ->
      val sharesOwnedList = accumulator.sharesOwnedMap[record.record.symbol] ?: persistentListOf()
      val lastSharesOwned = sharesOwnedList.lastOrNull()
      val lastTotalShares = lastSharesOwned?.totalShares ?: BigDecimal("0")
      val replaceLastSharesOwned = lastSharesOwned?.dateRangeStart == record.record.date

      val totalShares =
          when (record.record.action) {
            Action.BUY,
            Action.BONUS -> lastTotalShares + record.record.shares
            Action.SELL -> lastTotalShares - record.record.shares
            else -> BigDecimal("0")
          }

      val newSharesOwned =
          SharesOwned(
              id = TypedId(),
              userId = downloaderConfig.userId,
              portfolioId = portfolioId,
              dateRangeStart = record.record.date,
              dateRangeEnd = MAX_DATE,
              symbol = record.record.symbol,
              totalShares = totalShares)

      val newMap =
          accumulator.sharesOwnedMap.mutate { map ->
            map[record.record.symbol] =
                sharesOwnedList.mutate { list ->
                  if (replaceLastSharesOwned) {
                    list[list.size - 1] = newSharesOwned
                  } else {
                    lastSharesOwned?.let { lastSharesOwnedReal ->
                      list[list.size - 1] =
                          lastSharesOwnedReal.copy(dateRangeEnd = record.record.date.minusDays(1))
                    }
                    list += newSharesOwned
                  }
                }
          }
      accumulator.copy(sharesOwnedMap = newMap)
    }
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

  private fun getTransactionDataFromSpreadsheet(
      config: PortfolioConfig,
      accessToken: String
  ): ResponseSpec {
    log.debug(
        "Downloading data from spreadsheet. Sheet: ${config.sheetId} Values: ${config.valuesRange}")
    return webClient
        .get()
        .uri(
            "${downloaderConfig.googleSheetsApiBaseUrl}/spreadsheets/${config.sheetId}/values/${config.valuesRange}")
        .header("Authorization", "Bearer $accessToken")
        .retrieveSuccess()
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

private data class TotalSharesHolder(val shares: BigDecimal, val date: LocalDate)

private data class OwnershipContext(
    val sharesOwnedMap: PersistentMap<String, PersistentList<SharesOwned>>,
    val record: CraigMillerTransactionRecord
)
