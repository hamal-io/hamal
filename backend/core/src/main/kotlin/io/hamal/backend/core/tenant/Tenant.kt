package io.hamal.backend.core.tenant

import io.hamal.lib.core.vo.TenantId
import kotlinx.serialization.Serializable

interface Tenant {
    val id: TenantId

    @Serializable
    data class Active(
        override val id: TenantId
    ) : Tenant
}