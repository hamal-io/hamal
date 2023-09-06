package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable

@Serializable
data class HubCreateAccountReq(
    val name: AccountName,
    val email: AccountEmail?,
    val password: Password?
)

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
    fun create(createAccountReq: HubCreateAccountReq): HubSubmittedWithTokenReq
}

internal class DefaultHubAccountService(
    private val template: HttpTemplate
) : HubAccountService {

    override fun create(createAccountReq: HubCreateAccountReq) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(HubSubmittedWithTokenReq::class)

}