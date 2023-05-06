package io.hamal.lib

import io.hamal.lib.vo.TenantId
import kotlinx.serialization.Serializable

@Serializable
class Requester<ID>(
    val type: Type,
    val value: ID
) {
    companion object {
        @JvmStatic
        fun tenant(value: TenantId): Requester<TenantId> {
            return Requester(Type.Tenant, value)
        }
    }

    enum class Type {
        Tenant
    }
}
