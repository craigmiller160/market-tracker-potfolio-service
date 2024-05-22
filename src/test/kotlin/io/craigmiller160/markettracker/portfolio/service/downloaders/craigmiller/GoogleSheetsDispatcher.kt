package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import io.craigmiller160.markettracker.portfolio.config.PortfolioConfig
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class GoogleSheetsDispatcher(
    private val baseUrl: String,
    private val spreadsheetUrlValues: List<PortfolioConfig>,
    private val expectedToken: String,
    private val response: String
) : Dispatcher() {
  override fun dispatch(request: RecordedRequest): MockResponse {
    try {
      println("Received request: ${request.method ?: ""} ${request.requestUrl?.toString() ?: ""}")

      val authHeader =
          request.headers["Authorization"] ?: return MockResponse().setResponseCode(401)
      if (authHeader != "Bearer $expectedToken") {
        val errorMessage = "Missing expected authorization header"
        System.err.println(errorMessage)
        return MockResponse()
            .setResponseCode(401)
            .setHeader("Content-Type", "text/plain")
            .setBody(errorMessage)
      }

      val url = request.requestUrl?.toString() ?: ""
      val expectedUrlRegex =
          Regex("^${baseUrl}/spreadsheets/(?<sheetId>.+)/values/(?<valuesRange>.+)\$")
      val matchResult = expectedUrlRegex.find(url)

      if (matchResult == null) {
        val errorMessage = "Request URL does not match regex: $url"
        System.err.println(errorMessage)
        return MockResponse()
            .setResponseCode(404)
            .setHeader("Content-Type", "text/plain")
            .setBody(errorMessage)
      }

      val sheetId = matchResult.groups["sheetId"]?.value ?: ""
      val valuesRange = matchResult.groups["valuesRange"]?.value ?: ""
      val matchingUrlValues =
          spreadsheetUrlValues.find { values ->
            values.sheetId == sheetId && values.valuesRange == valuesRange
          }

      if (matchingUrlValues == null) {
        val errorMessage =
            "Request URL does not have required path elements: SheetId=$sheetId ValuesRange=$valuesRange"
        System.err.println(errorMessage)
        return MockResponse()
            .setResponseCode(404)
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
