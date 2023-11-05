package io.hamal.request

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password

interface CreateAccountReq {
    val name: AccountName
    val email: AccountEmail?
    val password: Password?
}

interface CreateAnonymousAccountReq {
    val id: AccountId
    val name: AccountName
}

interface ConvertAnonymousAccountReq {
    val name: AccountName?
    val password: Password
    val email: AccountEmail?
}