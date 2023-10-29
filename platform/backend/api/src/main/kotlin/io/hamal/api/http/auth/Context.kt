package io.hamal.api.http.auth

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.Account
import io.hamal.repository.api.Auth

data class AuthContext(
    val auth: Auth,
    val account: Account,
    val token: AuthToken
) {
    val accountId get() = auth.accountId
}

object AuthContextHolder {
    fun set(context: AuthContext) {
        store.set(context)
    }

    fun get(): AuthContext = store.get()!!
    fun clear() = store.remove()
    private val store = ThreadLocal<AuthContext?>()
}