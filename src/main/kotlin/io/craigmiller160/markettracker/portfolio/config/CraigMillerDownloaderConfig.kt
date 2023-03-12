package io.craigmiller160.markettracker.portfolio.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "downloaders.craigmiller")
data class CraigMillerDownloaderConfig(
    val googleSheetsApiBaseUrl: String,
    val serviceAccountJsonPath: String,
    val spreadsheetId: String,
    val valuesRange: String
)
