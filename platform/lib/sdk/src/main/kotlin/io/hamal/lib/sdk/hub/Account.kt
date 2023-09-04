package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

@Serializable
data class HubAccountList(
    val accounts: List<Account>
) {
    @Serializable
    data class Account(
        val id: AccountId,
        val name: AccountName
    )
}

@Serializable
data class HubAccount(
    val id: AccountId,
    val name: AccountName,
)

interface HubAccountService

internal class DefaultHubAccountService(
    private val template: HttpTemplate
) : HubAccountService