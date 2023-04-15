package io.craigmiller160.markettracker.portfolio.domain.client

import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
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
    TODO()
  }

  @Test
  fun `query with params`() {
    TODO()
  }

  @Test
  fun `update without params`() {
    TODO()
  }

  @Test
  fun `update with params`() {
    TODO()
  }

  @Test
  fun `batch update without params`() {
    TODO()
  }

  @Test
  fun `batch update with params`() {
    TODO()
  }
}
