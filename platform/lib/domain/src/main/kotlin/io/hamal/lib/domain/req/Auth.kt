package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import kotlinx.serialization.Serializable

@Serializable
data class SignInReq(
    val name: AccountName,
    val password: Password?
)