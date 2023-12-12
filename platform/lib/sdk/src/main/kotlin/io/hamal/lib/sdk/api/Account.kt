package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.ConvertAnonymousAccountReq
import io.hamal.request.CreateAccountReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAccountCreateReq(
    override val email: Email,
    override val password: Password
) : CreateAccountReq

@Serializable
data class ApiAnonymousAccountConvertReq(
    override val email: Email,
    override val password: Password
) : ConvertAnonymousAccountReq


@Serializable
data class ApiAccountList(
    val accounts: List<Account>
) {
    @Serializable
    data class Account(
        val id: AccountId,
    )
}

@Serializable
data class ApiAccount(
    val id: AccountId
)

interface ApiAccountService {
    fun create(createAccountReq: ApiAccountCreateReq): ApiTokenSubmitted
}

internal class ApiAccountServiceImpl(
    private val template: HttpTemplate
) : ApiAccountService {

    override fun create(createAccountReq: ApiAccountCreateReq) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(ApiTokenSubmitted::class)

}