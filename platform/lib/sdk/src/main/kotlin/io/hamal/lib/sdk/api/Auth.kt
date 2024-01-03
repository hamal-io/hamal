package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.domain.request.AuthChallengeMetaMaskRequest
import io.hamal.lib.domain.request.AuthLogInEmailRequest
import io.hamal.lib.domain.request.AuthLogInMetaMaskRequest

data class ApiAuthChallengeMetaMaskRequest(
    override val address: Web3Address,
) : AuthChallengeMetaMaskRequest

data class ApiChallengeMetaMask(
    val challenge: Web3Challenge
)

data class ApiAuthLoginMetaMaskRequest(
    override val address: Web3Address,
    override val signature: Web3Signature
) : AuthLogInMetaMaskRequest

data class ApiAuthLoginEmailRequest(
    override val email: Email,
    override val password: Password
) : AuthLogInEmailRequest

data class ApiTokenRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val defaultFlowIds: List<GroupDefaultFlowId>,
    val token: AuthToken
) : ApiRequested

data class ApiAccountConvertRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val accountId: AccountId,
    val token: AuthToken
) : ApiRequested

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplate
) : AuthService