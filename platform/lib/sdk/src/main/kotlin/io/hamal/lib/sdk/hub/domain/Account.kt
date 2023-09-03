package io.hamal.lib.sdk.hub.domain

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import kotlinx.serialization.Serializable

@Serializable
data class ApiAccountList(
    val accounts: List<ApiSimpleAccount>
) {
    @Serializable
    data class ApiSimpleAccount(
        val id: AccountId,
        val name: AccountName
    )
}


@Serializable
data class ApiAccount(
    val id: AccountId,
    val name: AccountName,
)