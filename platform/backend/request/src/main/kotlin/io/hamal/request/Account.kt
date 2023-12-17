package io.hamal.request

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.domain.vo.Web3Address

interface AccountCreateReq {
    val email: Email
    val password: Password
}

interface AccountCreateAnonymousReq {
    val id: AccountId
}

interface AccountCreateMetaMaskReq {
    val id: AccountId
    val address: Web3Address
}

interface AccountConvertAnonymousReq {
    val email: Email
    val password: Password
}