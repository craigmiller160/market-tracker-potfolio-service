package io.craigmiller160.markettracker.portfolio.domain.client

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient

@MarketTrackerPortfolioIntegrationTest
class CoroutineDatabaseClientTest
@Autowired
constructor(
    private val coroutineClient: CoroutineDatabaseClient,
    private val client: DatabaseClient
) {
  @Test
  fun `query without params`() {
    runBlocking {
      insertPortfolio("abc")
      insertPortfolio("def")
    }

    val result = runBlocking {
      coroutineClient.query("SELECT COUNT(*) AS the_count FROM portfolios").map { list ->
        list.first().get("count")
      }
    }
    result.shouldBeRight(2L)
  }

  @Test
  fun `query with params`() {
    val expectedId = runBlocking {
      val first = insertPortfolio("abc")
      insertPortfolio("def")
      first.id.value
    }

    val result = runBlocking {
      coroutineClient
          .query("SELECT id FROM portfolios WHERE name = :name", mapOf("name" to "abc"))
          .map { list -> list.map { it.get("id") } }
    }
    result.shouldBeRight(listOf(expectedId))
  }

  @Test
  fun `update without params`() {
    runBlocking {
      insertPortfolio("abc")
      insertPortfolio("def")
    }

    val result = runBlocking { coroutineClient.update("UPDATE portfolios SET name = name || '2'") }
    result.shouldBeRight(2L)
  }

  @Test
  fun `update with params`() {
    val firstId = runBlocking {
      val first = insertPortfolio("abc")
      insertPortfolio("def")
      first.id.value
    }

    val result = runBlocking {
      coroutineClient.update(
          "UPDATE portfolios SET name = name || '2' WHERE id = :id", mapOf("id" to firstId))
    }
    result.shouldBeRight(1L)
  }

  @Test
  fun `batch update without params`() {
    TODO()
  }

  @Test
  fun `batch update with params`() {
    TODO()
  }

  private suspend fun insertPortfolio(name: String): Portfolio {
    val portfolio = BasePortfolio(id = TypedId(), userId = TypedId(), name = name)
    client
        .sql("INSERT INTO portfolios (id, user_id, name) VALUES (:id, :userId, :name)")
        .bind("id", portfolio.id.value)
        .bind("userId", portfolio.userId.value)
        .bind("name", portfolio.name)
        .fetch()
        .rowsUpdated()
        .awaitSingle()
    return portfolio
  }
}
