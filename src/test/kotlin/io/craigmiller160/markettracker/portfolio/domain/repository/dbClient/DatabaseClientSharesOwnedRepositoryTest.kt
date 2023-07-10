package io.craigmiller160.markettracker.portfolio.domain.repository.dbClient

import io.craigmiller160.markettracker.portfolio.common.typedid.PortfolioId
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwnedInterval
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import java.math.BigDecimal
import java.time.LocalDate
import java.util.stream.Stream
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired

@MarketTrackerPortfolioIntegrationTest
class DatabaseClientSharesOwnedRepositoryTest
@Autowired
constructor(
    private val client: CoroutineDatabaseClient,
    private val sharesOwnedRepo: DatabaseClientSharesOwnedRepository
) {
  companion object {
    private val USER_1_ID = TypedId<UserId>()
    private val USER_2_ID = TypedId<UserId>()
    private val PORTFOLIO_1_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_2_ID = TypedId<PortfolioId>()
    private val PORTFOLIO_3_ID = TypedId<PortfolioId>()

    @JvmStatic
    fun sharesOwnedAtIntervalForPortfolio(): Stream<SharesAtIntervalAttempt> {
      val oneDay =
          listOf(
              LocalDate.of(2023, 1, 30) to BigDecimal("5"),
              LocalDate.of(2023, 1, 31) to BigDecimal("5"),
              LocalDate.of(2023, 2, 1) to BigDecimal("10"),
              LocalDate.of(2023, 2, 2) to BigDecimal("10"))

      val oneWeek =
          listOf(
              LocalDate.of(2023, 1, 22) to BigDecimal("5"),
              LocalDate.of(2023, 1, 29) to BigDecimal("5"),
              LocalDate.of(2023, 2, 5) to BigDecimal("5"))

      val oneMonth =
          listOf(
              LocalDate.of(2023, 1, 1) to BigDecimal("5"),
              LocalDate.of(2023, 2, 1) to BigDecimal("10"),
              LocalDate.of(2023, 3, 1) to BigDecimal("15"))

      return Stream.of(
          SharesAtIntervalAttempt(
              SharesOwnedInterval.DAILY,
              LocalDate.of(2023, 1, 30),
              LocalDate.of(2023, 2, 2),
              oneDay),
          SharesAtIntervalAttempt(
              SharesOwnedInterval.WEEKLY,
              LocalDate.of(2023, 1, 22),
              LocalDate.of(2023, 2, 5),
              oneWeek),
          SharesAtIntervalAttempt(
              SharesOwnedInterval.MONTHLY,
              LocalDate.of(2023, 1, 1),
              LocalDate.of(2023, 3, 1),
              oneMonth))
    }

    @JvmStatic fun sharesOwnedAtIntervalForUser(): Stream<SharesAtIntervalAttempt> = TODO()
  }

  private fun executeScript(name: String, params: Map<String, Any>) =
      javaClass.classLoader
          .getResourceAsStream("sql/databaseClientSharesOwnedRepositoryTest/$name")!!
          .bufferedReader()
          .readText()
          .let { sql -> runBlocking { client.update(sql, params).shouldBeRight() } }

  @BeforeEach
  fun setup() {
    executeScript(
        "all_1.sql",
        mapOf(
            "portfolio1Id" to PORTFOLIO_1_ID.value,
            "portfolio2Id" to PORTFOLIO_2_ID.value,
            "portfolio3Id" to PORTFOLIO_3_ID.value,
            "user1Id" to USER_1_ID.value,
            "user2Id" to USER_2_ID.value))
  }

  @Test
  fun `gets the current shares owned for stock in portfolio`() {
    executeScript(
        "getsTheCurrentSharesOwnedForStockInPortfolio.sql",
        mapOf(
            "user1Id" to USER_1_ID.value,
            "user2Id" to USER_2_ID.value,
            "portfolio1Id" to PORTFOLIO_1_ID.value,
            "portfolio2Id" to PORTFOLIO_2_ID.value,
            "portfolio3Id" to PORTFOLIO_3_ID.value))

    runBlocking {
          sharesOwnedRepo.getCurrentSharesOwnedForStockInPortfolio(USER_1_ID, PORTFOLIO_1_ID, "VTI")
        }
        .shouldBeRight(BigDecimal("20"))
  }

  @Test
  fun `gets the current shares owned for stock in all portfolios combined`() {
    executeScript(
        "getsTheCurrentSharesOwnedForStockInAllPortfoliosCombined.sql",
        mapOf(
            "user1Id" to USER_1_ID.value,
            "user2Id" to USER_2_ID.value,
            "portfolio1Id" to PORTFOLIO_1_ID.value,
            "portfolio2Id" to PORTFOLIO_2_ID.value,
            "portfolio3Id" to PORTFOLIO_3_ID.value))
    runBlocking { sharesOwnedRepo.getCurrentSharesOwnedForStockForUser(USER_1_ID, "VTI") }
        .shouldBeRight(BigDecimal("35"))
  }

  @ParameterizedTest
  @MethodSource("sharesOwnedAtIntervalForPortfolio")
  fun `gets the shares owned at an interval for stock in portfolio`(
      attempt: SharesAtIntervalAttempt
  ) {
    TODO()
  }

  @ParameterizedTest
  @MethodSource("sharesOwnedAtIntervalForUser")
  fun `gets the shares owned at an interval for stock in all portfolios combined`(
      attempt: SharesOwnedInterval
  ) {
    TODO()
  }
}

data class SharesAtIntervalAttempt(
    val interval: SharesOwnedInterval,
    val start: LocalDate,
    val end: LocalDate,
    val expected: List<Pair<LocalDate, BigDecimal>>
)
