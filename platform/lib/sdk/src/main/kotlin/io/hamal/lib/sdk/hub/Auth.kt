package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.SignInReq
import kotlinx.serialization.Serializable

@Serializable
data class HubSignInReq(
    override val name: AccountName,
    override val password: Password
) : SignInReq

interface HubAuthService

internal class DefaultHubAuthService(
    private val template: HttpTemplate
) : HubAuthService