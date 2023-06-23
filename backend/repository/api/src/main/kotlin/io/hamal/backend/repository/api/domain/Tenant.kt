package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.vo.TenantId
import kotlinx.serialization.Serializable

@Serializable
sealed interface Tenant {
    val id: TenantId

}

@Serializable
data class ActiveTenant(
    override val id: TenantId
) : Tenant