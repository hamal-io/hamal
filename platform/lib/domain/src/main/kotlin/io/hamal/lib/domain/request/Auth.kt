package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface AuthLogInEmailRequest {
    val email: Email
    val password: Password
}

data class AuthLoginEmailRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AuthId,
    val accountId: AccountId,
    val workspaceIds: List<WorkspaceId>,
    val hash: PasswordHash,
    val token: AuthToken,
) : Requested()


interface AuthChallengeMetaMaskRequest {
    val address: Web3Address
}

interface AuthLogInMetaMaskRequest {
    val address: Web3Address
    val signature: Web3Signature
}

data class AuthLoginMetaMaskRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AuthId,
    val accountId: AccountId,
    val workspaceIds: List<WorkspaceId>,
    val token: AuthToken,
    val address: Web3Address,
    val signature: Web3Signature
) : Requested()

data class AuthLogoutRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AccountId
) : Requested()

interface AuthUpdatePasswordRequest {
    val currentPassword: Password
    val newPassword: Password
}

data class AuthUpdatePasswordRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override val requestStatus: RequestStatus,
    val id: AuthId,
    val hash: PasswordHash,
) : Requested()

interface Auth