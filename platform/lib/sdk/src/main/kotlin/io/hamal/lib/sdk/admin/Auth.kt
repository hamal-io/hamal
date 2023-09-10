package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

@Serializable
data class AdminSignInReq(
    val name: AccountName,
    val password: Password?
)

interface AdminAuthService

internal class DefaultAdminAuthService(
    private val template: HttpTemplate
) : AdminAuthService