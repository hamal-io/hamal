package io.hamal.lib.sdk.api

import io.hamal.lib.domain.request.AccountConvertAnonymousRequest
import io.hamal.lib.domain.request.AccountCreateRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiAccountCreateRequest(
    override val email: Email,
    override val password: Password
) : AccountCreateRequest

data class ApiAccountConvertAnonymousRequest(
    override val email: Email,
    override val password: Password
) : AccountConvertAnonymousRequest

data class ApiAccountList(
    val accounts: List<Account>
) : ApiObject() {
    data class Account(
        val id: AccountId,
    )
}


data class ApiAccountMe(
    val id: AccountId,
    val workspaces: List<Workspace>
) : ApiObject() {
    data class Workspace(
        val id: WorkspaceId
    )
}

data class ApiAccount(
    val id: AccountId
) : ApiObject()

interface ApiAccountService {
    fun create(createAccountReq: ApiAccountCreateRequest): ApiTokenRequested
    fun me(): ApiAccountMe
}

internal class ApiAccountServiceImpl(
    private val template: HttpTemplate
) : ApiAccountService {

    override fun create(createAccountReq: ApiAccountCreateRequest) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(ApiTokenRequested::class)

    override fun me(): ApiAccountMe =
        template.get("/v1/accounts/me")
            .execute()
            .fold(ApiAccountMe::class)

}