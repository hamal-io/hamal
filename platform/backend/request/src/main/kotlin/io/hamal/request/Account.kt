package io.hamal.request

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.domain.vo.Web3Address

interface CreateAccountReq {
    val email: Email
    val password: Password
}

interface CreateAnonymousAccountReq {
    val id: AccountId
}

interface CreateMetaMaskAccountReq {
    val id: AccountId
    val address: Web3Address
}

interface ConvertAnonymousAccountReq {
    val email: Email
    val password: Password
}