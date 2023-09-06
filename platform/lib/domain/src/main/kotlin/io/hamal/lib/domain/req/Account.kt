package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountReq(
    val name: AccountName,
    val email: AccountEmail?,
    val password: Password?
)


@Serializable
data class CreateRootAccountReq(
    val name: AccountName,
    val email: AccountEmail,
    val password: Password
)