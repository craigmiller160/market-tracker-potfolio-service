package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import arrow.core.Either
import arrow.core.continuations.either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import io.craigmiller160.markettracker.portfolio.service.downloaders.DownloadParsingException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Action(val label: String) {
  BONUS("Bonus"),
  DEPOSIT("Deposit"),
  BUY("Buy"),
  SELL("Sell"),
  WITHDRAWAL("Withdrawal"),
  DIVIDEND("Dividend"),
  CASH_BALANCE("Cash Balance"),
  INVESTMENT_BALANCE("Investment Balance");

  companion object
}

data class CraigMillerTransactionRecord(
    val date: LocalDate,
    val action: Action,
    val amount: BigDecimal,
    val symbol: String,
    val shares: BigDecimal
) {
  companion object
}

fun Action.Companion.fromLabel(label: String): TryEither<Action> =
    Action.values().find { it.label == label }.leftIfNull("Invalid label for action: $label")

private val transactionDateFormat = DateTimeFormatter.ofPattern("M/d/yyyy")

fun CraigMillerTransactionRecord.Companion.fromRaw(
    rawRecord: List<String>
): TryEither<CraigMillerTransactionRecord> {
  val maxSize = if (rawRecord.size < 5) rawRecord.size else 5
  val rawValidFields = rawRecord.slice(0 until maxSize).filter { it.trim().isNotEmpty() }

  val dateResult = Either.catch { LocalDate.parse(rawValidFields[0], transactionDateFormat) }
  val actionResult = Action.fromLabel(rawValidFields[1])
  val amountResult =
      Either.catch {
        rawValidFields[2].replace(Regex("^\\$"), "").replace(",", "").let { BigDecimal(it) }
      }

  val symbol = if (rawValidFields.size >= 4) rawValidFields[3] else ""
  val sharesResult =
      if (rawValidFields.size >= 5) Either.catch { BigDecimal(rawValidFields[4]) }
      else Either.Right(BigDecimal("0"))

  return either
      .eager {
        CraigMillerTransactionRecord(
            date = dateResult.bind(),
            action = actionResult.bind(),
            amount = amountResult.bind(),
            symbol = symbol,
            shares = sharesResult.bind())
      }
      .mapLeft { ex -> DownloadParsingException("Error parsing raw download: $rawRecord", ex) }
}

private fun convertResult(result: Int): Int {
  if (result < 0) {
    return -1
  }

  if (result > 0) {
    return 1
  }

  return 0
}

val CraigMillerTransactionRecord.Companion.comparator: Comparator<CraigMillerTransactionRecord>
  get() = Comparator { one, two ->
    val symbolCompare = one.symbol.compareTo(two.symbol)
    if (symbolCompare != 0) {
      return@Comparator convertResult(symbolCompare)
    }

    val dateCompare = one.date.compareTo(two.date)
    if (dateCompare != 0) {
      return@Comparator convertResult(dateCompare)
    }

    return@Comparator convertResult(one.action.compareTo(two.action))
  }
