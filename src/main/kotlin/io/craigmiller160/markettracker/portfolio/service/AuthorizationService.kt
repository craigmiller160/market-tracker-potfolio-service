package io.craigmiller160.markettracker.portfolio.service

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.markettracker.portfolio.common.typedid.toTypedId
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class AuthorizationService {
  suspend fun getUserId(): TypedId<UserId> =
      ReactiveSecurityContextHolder.getContext()
          .awaitSingle()
          .authentication
          .let { it.principal as Jwt }
          .let { it.subject.toTypedId() }
}
