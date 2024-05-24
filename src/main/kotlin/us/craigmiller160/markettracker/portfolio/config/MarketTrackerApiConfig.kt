package io.craigmiller160.markettracker.portfolio.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "market-tracker-api")
data class MarketTrackerApiConfig(val host: String)
