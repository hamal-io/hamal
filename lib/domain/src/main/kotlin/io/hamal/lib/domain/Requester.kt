package io.hamal.lib.domain

import io.hamal.lib.domain.vo.AccountId
import kotlinx.serialization.Serializable

@Serializable
class Requester<ID>(
    val type: Type,
    val value: ID
) {
    companion object {
        @JvmStatic
        fun tenant(value: AccountId): Requester<AccountId> {
            return Requester(Type.Tenant, value)
        }
    }

    enum class Type {
        Tenant
    }
}
