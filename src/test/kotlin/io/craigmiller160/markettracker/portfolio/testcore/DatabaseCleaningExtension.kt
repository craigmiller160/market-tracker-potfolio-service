package io.craigmiller160.markettracker.portfolio.testcore

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.test.context.junit.jupiter.SpringExtension

class DatabaseCleaningExtension : BeforeEachCallback, AfterEachCallback {
  companion object {
    private const val DELETE_PORTFOLIOS_SQL = "DELETE FROM portfolios"
    private const val DELETE_SHARES_OWNED_SQL = "DELETE FROM shares_owned"
  }
  override fun beforeEach(context: ExtensionContext) {
    runBlocking { cleanDatabase(context) }
  }

  override fun afterEach(context: ExtensionContext) {
    runBlocking { cleanDatabase(context) }
  }

  private fun getDatabaseClient(context: ExtensionContext) =
      SpringExtension.getApplicationContext(context).getBean(DatabaseClient::class.java)

  private suspend fun cleanDatabase(context: ExtensionContext) {
    val databaseClient = getDatabaseClient(context)
    databaseClient.sql(DELETE_SHARES_OWNED_SQL).fetch().awaitRowsUpdated()

    databaseClient.sql(DELETE_PORTFOLIOS_SQL).fetch().awaitRowsUpdated()
  }
}
