package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.SignInReq
import kotlinx.serialization.Serializable

@Serializable
data class AdminSignInReq(
    override val name: AccountName,
    override val password: Password
) : SignInReq

interface AdminAuthService

internal class AdminAuthServiceImpl(
    private val template: HttpTemplate
) : AdminAuthService