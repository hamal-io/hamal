package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.vo.AccountId
import kotlinx.serialization.Serializable

interface Tenant {
    val id: AccountId

    @Serializable
    data class Active(
        override val id: AccountId
    ) : Tenant
}