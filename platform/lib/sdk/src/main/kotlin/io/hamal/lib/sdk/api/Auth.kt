package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.request.SignInReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiSignInReq(
    override val name: AccountName,
    override val password: Password
) : SignInReq

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplateImpl
) : AuthService