package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.markettracker.portfolio.domain.client.CoroutineDatabaseClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class DatabaseConfig {
  @Bean
  fun coroutineDatabaseClient(databaseClient: DatabaseClient): CoroutineDatabaseClient =
      CoroutineDatabaseClient(databaseClient)
}
