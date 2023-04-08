package io.craigmiller160.markettracker.portfolio.testutils

import io.craigmiller160.markettracker.portfolio.common.typedid.TypedId
import io.craigmiller160.markettracker.portfolio.common.typedid.UserId
import io.craigmiller160.testcontainers.common.core.AuthenticationHelper

data class DefaultUsers(
    val primaryUser: AuthenticationHelper.TestUserWithToken,
    val secondaryUser: AuthenticationHelper.TestUserWithToken,
    val tertiaryUser: AuthenticationHelper.TestUserWithToken
)

val AuthenticationHelper.TestUserWithToken.userTypedId: TypedId<UserId>
  get() = TypedId(this.userId)
