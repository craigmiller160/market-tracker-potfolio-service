package io.craigmiller160.markettracker.portfolio.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "downloaders.craigmiller")
data class CraigMillerDownloaderConfig(
    val googleSheetsApiBaseUrl: String,
    val serviceAccountJsonPath: String,
    val portfolioSpreadsheets: List<PortfolioConfig>
)

data class PortfolioConfig(val name: String, val sheetId: String, val valuesRange: String)
