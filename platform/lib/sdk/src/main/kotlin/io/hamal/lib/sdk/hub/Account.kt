package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.req.CreateAccountReq
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
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

interface HubAccountService {
    fun create(createAccountReq: CreateAccountReq): HubSubmittedWithTokenReq
}

internal class DefaultHubAccountService(
    private val template: HttpTemplate
) : HubAccountService {

    override fun create(createAccountReq: CreateAccountReq) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(HubSubmittedWithTokenReq::class)

}