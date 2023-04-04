package io.craigmiller160.markettracker.portfolio.web.routes

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.models.SharesOwned
import io.craigmiller160.markettracker.portfolio.domain.models.toPortfolioNameResponse
import io.craigmiller160.markettracker.portfolio.domain.repository.PortfolioRepository
import io.craigmiller160.markettracker.portfolio.domain.repository.SharesOwnedRepository
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.craigmiller160.markettracker.portfolio.testutils.DefaultUsers
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@MarketTrackerPortfolioIntegrationTest
class PortfolioRoutesTest
@Autowired
constructor(
    private val webTestClient: WebTestClient,
    private val portfolioRepo: PortfolioRepository,
    private val sharesOwnedRepo: SharesOwnedRepository,
    private val defaultUsers: DefaultUsers,
    private val objectMapper: ObjectMapper
) {
  companion object {
    private const val DATE_RANGE_LENGTH = 10L
  }

  private val portfolios: List<Portfolio> =
      (0 until 5).map { index ->
        BasePortfolio(
            id = TypedId(),
            userId =
                if (index == 0) TypedId(defaultUsers.secondaryUser.userId)
                else TypedId(defaultUsers.primaryUser.userId),
            name = "Portfolio-$index")
      }
  private val stocks: List<String> = listOf("VTI", "VXUS", "VOO")
  private val baseDate = LocalDate.now()
  private val sharesOwned: List<SharesOwned> =
      portfolios.flatMapIndexed { portfolioIndex, portfolio ->
        val stocks =
            stocks.map { stock ->
              if (portfolioIndex == 1) {
                stock
              } else {
                "$stock-$portfolioIndex"
              }
            }

        stocks.mapIndexed { stockIndex, symbol ->
          val dateOffset = DATE_RANGE_LENGTH * stockIndex
          SharesOwned(
              id = TypedId(),
              portfolioId = portfolio.id,
              userId = portfolio.userId,
              dateRangeStart = baseDate.plusDays(dateOffset),
              dateRangeEnd = baseDate.plusDays(dateOffset + DATE_RANGE_LENGTH),
              symbol = symbol,
              totalShares = BigDecimal("${stockIndex + 1}"))
        }
      }

  @BeforeEach
  fun setup() {
    runBlocking {
      portfolioRepo.createAllPortfolios(portfolios)
      sharesOwnedRepo.createAllSharesOwned(sharesOwned)
    }
  }
  @Test
  fun `gets list of portfolio names for user`() {
    val expectedResponse = portfolios.drop(1).map { it.toPortfolioNameResponse() }
    webTestClient
        .get()
        .uri("/portfolios")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  @Test
  fun `gets list of stocks in portfolio for user`() {
    val expectedResponse = stocks.map { "$it-1" }.sorted()
    webTestClient
        .get()
        .uri("/portfolios/${portfolios[1].id}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }

  @Test
  fun `gets list of stocks in portfolio that the user does not own`() {
    webTestClient
        .get()
        .uri("/portfolios/${portfolios[0].id}")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(listOf<String>()))
  }

  @Test
  fun `gets a list of unique stocks for all portfolios combined`() {
    val expectedResponse =
        stocks
            .flatMap { stock ->
              (1..4).map { index ->
                if (index == 1) {
                  stock
                } else {
                  "$stock-$index"
                }
              }
            }
            .distinct()
            .sorted()
    webTestClient
        .get()
        .uri("/portfolios/combined")
        .header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody()
        .json(objectMapper.writeValueAsString(expectedResponse))
  }
}
