package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable

@Serializable
data class AdminCreateAccountReq(
    val name: AccountName,
    val email: AccountEmail?,
    val password: Password?
)

@Serializable
data class AdminAccountList(
    val accounts: List<Account>
) {
    @Serializable
    data class Account(
        val id: AccountId,
        val name: AccountName
    )
}

@Serializable
data class AdminAccount(
    val id: AccountId,
    val name: AccountName,
)

interface AdminAccountService {
    fun create(createAccountReq: AdminCreateAccountReq): AdminSubmittedWithTokenReq
}

internal class DefaultAdminAccountService(
    private val template: HttpTemplate
) : AdminAccountService {

    override fun create(createAccountReq: AdminCreateAccountReq) =
        template.post("/v1/accounts")
            .body(createAccountReq)
            .execute()
            .fold(AdminSubmittedWithTokenReq::class)

}