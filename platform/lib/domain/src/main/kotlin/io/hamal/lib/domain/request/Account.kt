package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface AccountCreateRequest {
    val email: Email
    val password: Password
}

data class AccountCreateRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val accountId: AccountId,
    val accountType: AccountType,
    val passwordAuthId: AuthId,
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
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val accountId: AccountId,
    val accountType: AccountType,
    val passwordAuthId: AuthId,
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
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val accountId: AccountId,
    val accountType: AccountType,
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
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val accountId: AccountId,
    val passwordAuthId: AuthId,
    val tokenAuthId: AuthId,
    val email: Email,
    val hash: PasswordHash,
    val token: AuthToken
) : Requested()

data class AccountCreateRootRequest(
    val email: Email,
    val password: Password
)