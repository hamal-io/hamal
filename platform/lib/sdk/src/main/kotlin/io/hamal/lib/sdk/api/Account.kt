package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.AccountConvertAnonymousReq
import io.hamal.request.AccountCreateReq

data class ApiAccountCreateReq(
    override val email: Email,
    override val password: Password
) : AccountCreateReq

data class ApiAccountConvertAnonymousReq(
    override val email: Email,
    override val password: Password
) : AccountConvertAnonymousReq

data class ApiAccountList(
    val accounts: List<Account>
) {
    data class Account(
        val id: AccountId,
    )
}

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