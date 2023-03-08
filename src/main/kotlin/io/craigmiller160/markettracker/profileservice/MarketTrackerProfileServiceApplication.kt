package io.craigmiller160.markettracker.profileservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class MarketTrackerProfileServiceApplication

fun main(args: Array<String>) {
  runApplication<MarketTrackerProfileServiceApplication>(*args)
}
