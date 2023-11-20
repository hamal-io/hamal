package io.hamal.request

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.Password

interface LogInReq {
    val name: AccountName
    val password: Password
}

interface LogOutReq {
    val token: AuthToken
}