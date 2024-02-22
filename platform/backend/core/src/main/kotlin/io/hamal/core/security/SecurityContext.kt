package io.hamal.core.security

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.repository.api.Auth

object SecurityContext {

    fun <T : Any> with(auth: Auth, action: () -> T): T {
        try {
            store.set(auth)
            return action()
        } finally {
            store.remove()
        }
    }

    private val store = ThreadLocal<Auth?>()

    val current: Auth get() = store.get() ?: Auth.Anonymous

    val currentAuthId: AuthId get() = current.id

    val currentAccountId: AccountId
        get() = run {
            val currentAuth = current
            if (currentAuth !is Auth.Account) {
                throw IllegalStateException("Not an account")
            }
            currentAuth.accountId
        }
}