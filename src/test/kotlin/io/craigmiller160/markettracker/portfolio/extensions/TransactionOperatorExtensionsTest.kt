package io.craigmiller160.markettracker.portfolio.extensions

import io.craigmiller160.markettracker.portfolio.testcore.MarketTrackerPortfolioIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient

@MarketTrackerPortfolioIntegrationTest
class TransactionOperatorExtensionsTest
@Autowired
constructor(private val databaseClient: DatabaseClient) {
  @Test
  fun `commits changes for Right`() {
    TODO()
  }

  @Test
  fun `rolls back changes for Left`() {
    TODO()
  }
}
