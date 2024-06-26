package io.hamal.lib.sdk.api

import io.hamal.lib.domain.request.AuthChallengeMetaMaskRequest
import io.hamal.lib.domain.request.AuthLogInEmailRequest
import io.hamal.lib.domain.request.AuthLogInMetaMaskRequest
import io.hamal.lib.domain.request.AuthUpdatePasswordRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate

data class ApiAuthChallengeMetaMaskRequest(
    override val address: Web3Address,
) : AuthChallengeMetaMaskRequest

data class ApiChallengeMetaMask(
    val challenge: Web3Challenge
) : ApiObject()

data class ApiAuthLoginMetaMaskRequest(
    override val address: Web3Address,
    override val signature: Web3Signature
) : AuthLogInMetaMaskRequest

data class ApiAuthLoginEmailRequest(
    override val email: Email,
    override val password: Password
) : AuthLogInEmailRequest

data class ApiUpdatePasswordRequest(
    override val currentPassword: Password,
    override val newPassword: Password
) : AuthUpdatePasswordRequest

data class ApiUpdatePasswordRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus
) : ApiRequested()

data class ApiTokenRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: AccountId,
    val workspaceIds: List<WorkspaceId>,
    val token: AuthToken,
    val address: Web3Address? = null,
) : ApiRequested()

data class ApiAccountConvertRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: AccountId,
    val token: AuthToken
) : ApiRequested()

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplate
) : AuthService