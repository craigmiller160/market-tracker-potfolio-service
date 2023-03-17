package io.craigmiller160.markettracker.portfolio.service.downloaders.craigmiller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.zip
import io.craigmiller160.markettracker.portfolio.functions.KtResult
import io.craigmiller160.markettracker.portfolio.functions.ktRunCatching
import java.lang.IllegalArgumentException
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

fun Action.Companion.fromLabel(label: String): KtResult<Action> =
    Action.values().find { it.label == label }?.let { Ok(it) }
        ?: Err(IllegalArgumentException("Invalid label for action: $label"))

private val transactionDateFormat = DateTimeFormatter.ofPattern("M/d/yyyy")

fun CraigMillerTransactionRecord.Companion.fromRaw(
    rawRecord: List<String>
): KtResult<CraigMillerTransactionRecord> {
  val dateResult = ktRunCatching { LocalDate.parse(rawRecord[0], transactionDateFormat) }
  val actionResult = Action.fromLabel(rawRecord[1])
  val amountResult = ktRunCatching {
    rawRecord[2].replace(Regex("^\\$"), "").let { BigDecimal(it) }
  }
  val symbol = rawRecord[3]
  val sharesResult = ktRunCatching { BigDecimal(rawRecord[4]) }
  return zip({ dateResult }, { actionResult }, { amountResult }, { sharesResult }) {
      date,
      action,
      amount,
      shares ->
    CraigMillerTransactionRecord(
        date = date, action = action, amount = amount, shares = shares, symbol = symbol)
  }
}
