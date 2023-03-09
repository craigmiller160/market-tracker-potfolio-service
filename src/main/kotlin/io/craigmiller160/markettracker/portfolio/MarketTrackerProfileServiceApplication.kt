package io.craigmiller160.markettracker.portfolio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["io.craigmiller160.markettracker.portfolio"])
class MarketTrackerProfileServiceApplication

fun main(args: Array<String>) {
  runApplication<MarketTrackerProfileServiceApplication>(*args)
}
