package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.vo.TenantId
import kotlinx.serialization.Serializable

interface Tenant {
    val id: TenantId

    @Serializable
    data class Active(
        override val id: TenantId
    ) : Tenant
}