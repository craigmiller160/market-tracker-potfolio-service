package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.testutils.DataLoader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MarketTrackerDispatcher(private val host: String) : Dispatcher() {
  private val vtiHistory: String =
      DataLoader.load("data/craigmiller/TradierHistoryFor401k_VTI.json")
  private val vxusHistory: String =
      DataLoader.load("data/craigmiller/TradierHistoryFor401k_VXUS.json")

  override fun dispatch(request: RecordedRequest): MockResponse {
    try {
      val url = request.requestUrl?.toString() ?: ""
      println("Received request: ${request.method ?: ""} $url")

      // TODO validate authorization

      val end = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      val expectedUrlRegex =
          Regex(
              "^$host/tradier/markets/history\\?symbol=(?<symbol>.+)&start=2016-01-01&end=$end&interval=monthly$")
      println("URL Regex: $expectedUrlRegex")
      val matchResult = expectedUrlRegex.find(url)

      if (matchResult == null) {
        val errorMessage = "Request URL does not match regex: $url"
        System.err.println(errorMessage)
        return MockResponse()
            .setResponseCode(404)
            .setHeader("Content-Type", "text/plain")
            .setBody(errorMessage)
      }

      val symbol = matchResult.groups["symbol"]?.value ?: ""
      println("Symbol in Request: $symbol")
      val response =
          when (symbol) {
            "VTI" -> vtiHistory
            "VXUS" -> vxusHistory
            else -> null
          }

      if (response == null) {
        val errorMessage = "Unknown symbol: $symbol"
        System.err.println(errorMessage)
        return MockResponse()
            .setResponseCode(400)
            .setHeader("Content-Type", "text/plain")
            .setBody(errorMessage)
      }

      return MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(response)
    } catch (ex: Exception) {
      ex.printStackTrace()
      return MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "text/plain")
          .setBody(ex.toString())
    }
  }
}
