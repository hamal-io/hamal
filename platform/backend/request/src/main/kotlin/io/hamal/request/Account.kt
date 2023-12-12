package io.hamal.request

import io.hamal.lib.domain.vo.*

interface CreateAccountReq {
    val name: AccountName
    val email: AccountEmail?
    val password: Password?
}

interface CreateAnonymousAccountReq {
    val id: AccountId
    val name: AccountName
}

interface CreateMetaMaskAccountReq {
    val id: AccountId
    val name: AccountName
    val address: Web3Address
}

interface ConvertAnonymousAccountReq {
    val name: AccountName?
    val password: Password
    val email: AccountEmail?
}