package io.craigmiller160.markettracker.portfolio.config

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "downloaders.craigmiller")
data class CraigMillerDownloaderConfig(
    val userId: TypedId<UserId>,
    val googleSheetsApiBaseUrl: String,
    val serviceAccountJsonPath: String,
    val portfolioSpreadsheetsStandard: List<PortfolioConfigStandard>,
    val portfolioSpreadsheets401k: List<PortfolioConfig401k>
)

sealed interface PortfolioConfig {
  val name: String
  val sheetId: String
  val valuesRange: String
}

data class PortfolioConfigStandard(
    override val name: String,
    override val sheetId: String,
    override val valuesRange: String
) : PortfolioConfig

data class PortfolioConfig401k(
    override val name: String,
    override val sheetId: String,
    override val valuesRange: String
) : PortfolioConfig
