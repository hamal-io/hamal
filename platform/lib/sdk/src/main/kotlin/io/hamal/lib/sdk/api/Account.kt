package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateAccountReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateAccountReq(
    override val name: AccountName,
    override val email: AccountEmail?,
    override val password: Password?
) : CreateAccountReq

@Serializable
data class ApiAccountList(
    val accounts: List<Account>
) {
    @Serializable
    data class Account(
        val id: AccountId,
        val name: AccountName
    )
}

@Serializable
data class ApiAccount(
    val id: AccountId,
    val name: AccountName,
)

interface ApiAccountService {
    fun create(createAccountReq: ApiCreateAccountReq): ApiSubmittedWithTokenReq
}

internal class ApiAccountServiceImpl(
    private val template: HttpTemplate
) : ApiAccountService {

    override fun create(createAccountReq: ApiCreateAccountReq) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(ApiSubmittedWithTokenReq::class)

}