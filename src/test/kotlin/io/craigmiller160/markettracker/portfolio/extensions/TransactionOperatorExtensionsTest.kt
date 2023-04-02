package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import arrow.core.flatMap
import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.transaction.reactive.TransactionalOperator

@MarketTrackerPortfolioIntegrationTest
class TransactionOperatorExtensionsTest
@Autowired
constructor(
    private val databaseClient: DatabaseClient,
    private val transactionOperator: TransactionalOperator
) {
  @Test
  fun `commits changes for Right`() {
    runBlocking {
      countRecords().shouldBe(0)
      transactionOperator.executeAndAwaitEither { executeInsert() }.shouldBeRight(1)
      countRecords().shouldBe(1)
    }
  }

  @Test
  fun `rolls back changes for Left`() {
    runBlocking {
      countRecords().shouldBe(0)
      transactionOperator
          .executeAndAwaitEither { executeInsert().flatMap { Either.Left(RuntimeException()) } }
          .shouldBeLeft(RuntimeException())
      countRecords().shouldBe(0)
    }
  }

  private suspend fun countRecords(): Long =
      databaseClient
          .sql("SELECT COUNT(*) AS the_count FROM portfolios")
          .map { row, _ -> row.get("the_count").toString().toLong() }
          .awaitSingle()

  private suspend fun executeInsert(): TryEither<Long> =
      Either.catch {
        databaseClient
            .sql("INSERT INTO portfolios (id, user_id, name) VALUES (:id, :userId, :name)")
            .bind("id", UUID.randomUUID())
            .bind("userId", UUID.randomUUID())
            .bind("name", "Name-${UUID.randomUUID()}")
            .fetch()
            .rowsUpdated()
            .awaitSingle()
      }
}
