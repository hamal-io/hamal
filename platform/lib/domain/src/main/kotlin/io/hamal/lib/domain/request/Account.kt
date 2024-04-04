package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface AccountCreateRequest {
    val email: Email
    val password: Password
}

data class AccountCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val workspaceId: WorkspaceId,
    val accountId: AccountId,
    val accountType: AccountType,
    val emailAuthId: AuthId,
    val tokenAuthId: AuthId,
    val namespaceId: NamespaceId,
    val email: Email,
    val salt: PasswordSalt,
    val hash: PasswordHash,
    val token: AuthToken
) : Requested()

interface AccountCreateAnonymousRequest {
    val id: AccountId
}

data class AccountCreateAnonymousRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AccountId,
    val type: AccountType,
    val workspaceId: WorkspaceId,
    val emailAuthId: AuthId,
    val tokenAuthId: AuthId,
    val namespaceId: NamespaceId,
    val salt: PasswordSalt,
    val hash: PasswordHash,
    val token: AuthToken
) : Requested()

interface AccountCreateMetaMaskRequest {
    val id: AccountId
    val address: Web3Address
}

data class AccountCreateMetaMaskRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AccountId,
    val type: AccountType,
    val workspaceId: WorkspaceId,
    val metamaskAuthId: AuthId,
    val tokenAuthId: AuthId,
    val namespaceId: NamespaceId,
    val salt: PasswordSalt,
    val address: Web3Address,
    val token: AuthToken
) : Requested()

interface AccountConvertAnonymousRequest {
    val email: Email
    val password: Password
}

data class AccountConvertRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AccountId,
    val emailAuthId: AuthId,
    val tokenAuthId: AuthId,
    val email: Email,
    val hash: PasswordHash,
    val token: AuthToken
) : Requested()

data class AccountCreateRootRequest(
    val email: Email,
    val password: Password
)

interface AccountUpdateRequest {
    val password: Password
}

data class AccountUpdateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: AccountId,
    val hash: PasswordHash,
    val salt: PasswordSalt,
) : Requested()