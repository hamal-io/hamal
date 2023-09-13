package io.hamal.request

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password

interface CreateAccountReq {
    val name: AccountName
    val email: AccountEmail?
    val password: Password?
}