package io.craigmiller160.markettracker.portfolio.temp

import arrow.core.Either
import org.aopalliance.intercept.MethodInvocation
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.interceptor.TransactionAttributeSource
import org.springframework.transaction.interceptor.TransactionInterceptor

// TODO move into library if it works
@Component
@Primary
class CustomTransactionInterceptor(source: TransactionAttributeSource) : TransactionInterceptor() {
  init {
    transactionAttributeSource = source
  }

  override fun invoke(invocation: MethodInvocation): Any? {
    return try {
      super.invoke(WrappedMethodInvocation(invocation))
    } catch (ex: Exception) {
      when (ex) {
        is SpecialException -> ex.result
        else -> throw ex
      }
    }
  }
}

class SpecialException(val result: Either.Left<*>) : RuntimeException()

class WrappedMethodInvocation(private val invocation: MethodInvocation) :
    MethodInvocation by invocation {
  override fun proceed(): Any? {
    val result = invocation.proceed()
    if (result is Either.Left<*>) {
      throw SpecialException(result)
    }
    return result
  }
}
