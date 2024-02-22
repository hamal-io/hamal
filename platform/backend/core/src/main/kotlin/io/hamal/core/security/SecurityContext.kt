package io.hamal.core.security

import io.hamal.lib.domain.request.RequestedBy
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

    private val store = ThreadLocal<Auth>()

    val current: Auth get() = store.get()

    val currentAuthId: AuthId get() = current.id

    val currentAccountId : AccountId get() = current.accountId

    val requestedByCurrent: RequestedBy get() = RequestedBy.Authentication(currentAuthId)

}