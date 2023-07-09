package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.fold
import arrow.core.sequence
import arrow.typeclasses.Monoid
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.config.CraigMillerDownloaderConfig
import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MAX
import io.craigmiller160.markettracker.portfolio.domain.DATE_RANGE_MIN
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
    val RELEVANT_ACTIONS = listOf(Action.BONUS, Action.BUY, Action.SELL)
    val SYMBOL_EXCLUSIONS = listOf(Regex("^TBILL.*$"))
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
        .also { log.info("Completed download of Craig Miller portfolio data") }
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
          .filter { record -> !SYMBOL_EXCLUSIONS.any { regex -> regex.matches(record.symbol) } }
          .sortedWith(CraigMillerTransactionRecord.comparator)
          .map(initialRecord(portfolioId))
          .fold(ownershipContextMonoid(downloaderConfig.userId, portfolioId))
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
            dateRangeEnd = DATE_RANGE_MAX,
            symbol = record.symbol,
            totalShares = record.shares)
    OwnershipContext(
        sharesOwnedMap = persistentMapOf(record.symbol to persistentListOf(sharesOwned)),
        record = record)
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

private data class OwnershipContext(
    val sharesOwnedMap: PersistentMap<String, PersistentList<SharesOwned>>,
    val record: CraigMillerTransactionRecord? = null
)

private fun ownershipContextMonoid(
    userId: TypedId<UserId>,
    portfolioId: TypedId<PortfolioId>
): Monoid<OwnershipContext> =
    object : Monoid<OwnershipContext> {
      override fun empty(): OwnershipContext = OwnershipContext(sharesOwnedMap = persistentMapOf())

      override fun OwnershipContext.combine(b: OwnershipContext): OwnershipContext {
        val sharesOwnedList = this.sharesOwnedMap[b.record?.symbol] ?: persistentListOf()
        val lastSharesOwned = sharesOwnedList.lastOrNull()
        val lastTotalShares = lastSharesOwned?.totalShares ?: BigDecimal("0")
        val replaceLastSharesOwned =
            lastSharesOwned?.dateRangeStart == b.record?.date ?: DATE_RANGE_MIN

        val totalShares =
            when (b.record?.action) {
              Action.BUY,
              Action.BONUS -> lastTotalShares + b.record.shares
              Action.SELL -> lastTotalShares - b.record.shares
              else -> BigDecimal("0")
            }

        val newSharesOwned =
            SharesOwned(
                id = TypedId(),
                userId = userId,
                portfolioId = portfolioId,
                dateRangeStart = b.record?.date ?: DATE_RANGE_MIN,
                dateRangeEnd = DATE_RANGE_MAX,
                symbol = b.record?.symbol ?: "",
                totalShares = totalShares)

        val newMap =
            this.sharesOwnedMap.mutate { map ->
              map[b.record?.symbol ?: ""] =
                  sharesOwnedList.mutate { list ->
                    if (replaceLastSharesOwned) {
                      list[list.size - 1] = newSharesOwned
                    } else {
                      lastSharesOwned?.let { lastSharesOwnedReal ->
                        list[list.size - 1] =
                            lastSharesOwnedReal.copy(
                                dateRangeEnd = b.record?.date ?: DATE_RANGE_MAX)
                      }
                      list += newSharesOwned
                    }
                  }
            }
        return OwnershipContext(sharesOwnedMap = newMap)
      }
    }
