package io.craigmiller160.markettracker.portfolio.testcore

import io.craigmiller160.testcontainers.common.TestcontainersExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@SpringBootTest
@ExtendWith(value = [TestcontainersExtension::class, SpringExtension::class])
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(value = ["classpath:application.yml", "classpath:application-test.yml"])
annotation class MarketTrackerPortfolioIntegrationTest
