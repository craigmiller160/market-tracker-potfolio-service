package io.craigmiller160.markettracker.portfolio.extensions

import arrow.core.Either
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

suspend fun <E, T> TransactionalOperator.executeAndAwaitEither(
    fn: suspend (ReactiveTransaction) -> Either<E, T>
): Either<E, T> = executeAndAwait { tx ->
  fn(tx).mapLeft { ex ->
    tx.setRollbackOnly()
    ex
  }
}
