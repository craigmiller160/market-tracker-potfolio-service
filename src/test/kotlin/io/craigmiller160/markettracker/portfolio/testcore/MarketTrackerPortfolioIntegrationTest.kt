package io.craigmiller160.markettracker.portfolio.testcore

import io.craigmiller160.testcontainers.common.TestcontainersExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(value = [TestcontainersExtension::class, SpringExtension::class])
@AutoConfigureWebTestClient
@ActiveProfiles("test")
annotation class MarketTrackerPortfolioIntegrationTest
