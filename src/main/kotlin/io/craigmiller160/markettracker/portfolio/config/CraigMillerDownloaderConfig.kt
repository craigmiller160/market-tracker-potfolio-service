package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "downloaders.craigmiller")
data class CraigMillerDownloaderConfig(
    val userId: TypedId<UserId>,
    val googleSheetsApiBaseUrl: String,
    val serviceAccountJsonPath: String,
    val portfolioSpreadsheets: List<PortfolioConfig>
)

data class PortfolioConfig(val name: String, val sheetId: String, val valuesRange: String)
