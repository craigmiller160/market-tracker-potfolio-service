package io.craigmiller160.markettracker.portfolio.testutils

object DataLoader {
  fun load(path: String): String =
      javaClass.classLoader.getResourceAsStream(path).bufferedReader().readText()
}
