package com.nyanbot.security

import com.nyanbot.repository.Auth
import com.nyanbot.repository.HasAccountId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId

object SecurityContext {

    fun set(auth: Auth) {
        store.set(auth)
    }

    fun <T : Any> with(auth: Auth, action: () -> T): T {
        try {
            store.set(auth)
            return action()
        } finally {
            store.remove()
        }
    }

    val current: Auth get() = store.get() ?: Auth.Anonymous

    val currentAuthId: AuthId get() = current.id

    val currentAccountId: AccountId
        get() = run {
            val currentAuth = current
            if (currentAuth !is HasAccountId) {
                throw IllegalStateException("Not an account")
            }
            currentAuth.accountId
        }

    private val store = ThreadLocal<Auth?>()

}