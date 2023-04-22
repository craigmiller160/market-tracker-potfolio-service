package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.sequence
import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.domain.models.BasePortfolio
import io.craigmiller160.markettracker.portfolio.domain.models.Portfolio
import io.craigmiller160.markettracker.portfolio.domain.rowmappers.portfolioRowMapper
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

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
        list.first().getRequired("the_count", Long::class).getOrElse { throw it }
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
          .flatMap { list -> list.map { it.getRequired("id", UUID::class) }.sequence() }
    }
    result.shouldBeRight(listOf(expectedId))
  }

  @Test
  fun `query with params that include null`() {
    TODO()
  }

  @Test
  fun `query with row mapper`() {
    val portfolio = runBlocking { insertPortfolio("abc") }

    val result = runBlocking {
      coroutineClient.query("SELECT * FROM portfolios", portfolioRowMapper)
    }
    result.shouldBeRight(listOf(portfolio))
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
  fun `update with params that include null`() {
    TODO()
  }

  @Test
  fun `batch update without params`() {
    val result = runBlocking {
      coroutineClient.batchUpdate(
          "INSERT INTO portfolios (id, user_id, name) VALUES (:id, :userId, :name)")
    }
    result.shouldBeRight(listOf())

    val count = runBlocking {
      client.sql("SELECT COUNT(*) AS the_count FROM portfolios").fetch().awaitSingle()["the_count"]
    }
    count.shouldBe(0L)
  }

  @Test
  fun `batch update with params`() {
    val params = (0 until 3).map { listOf(UUID.randomUUID(), UUID.randomUUID(), "name-$it") }

    val result = runBlocking {
      coroutineClient.batchUpdate(
          "INSERT INTO portfolios (id, user_id, name) VALUES ($1, $2, $3)", params)
    }
    result.shouldBeRight(listOf(1L, 1L, 1L))

    val count = runBlocking {
      client.sql("SELECT COUNT(*) AS the_count FROM portfolios").fetch().awaitSingle()["the_count"]
    }
    count.shouldBe(3L)
  }

  @Test
  fun `batch update with params that include null`() {
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
