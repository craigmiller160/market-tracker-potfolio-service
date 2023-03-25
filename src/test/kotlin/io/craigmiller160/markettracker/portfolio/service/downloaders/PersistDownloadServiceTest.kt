package io.craigmiller160.markettracker.portfolio.service.downloaders

import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne

@MarketTrackerPortfolioIntegrationTest
class PersistDownloadServiceTest
@Autowired
constructor(
    private val databaseClient: DatabaseClient,
    private val persistDownloadService: PersistDownloadService
) {
  @Test
  fun `the portfolios are all persisted`() {
    runBlocking {
      getPortfolioCount().shouldBeEqualComparingTo(0)
      TODO()
    }
  }

  private suspend fun getPortfolioCount(): Long =
      databaseClient.sql("SELECT COUNT(*) AS count FROM portfolios").fetch().awaitOne()["count"]
          as Long
}
