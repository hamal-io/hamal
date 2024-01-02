package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class AccountCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val accountId: AccountId,
    val type: AccountType,
    val passwordAuthId: AuthId,
    val tokenAuthId: AuthId,
    val flowId: FlowId,
    val email: Email,
    val salt: PasswordSalt,
    val hash: PasswordHash,
    val token: AuthToken
) : Submitted

data class AccountCreateAnonymousSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val accountId: AccountId,
    val type: AccountType,
    val passwordAuthId: AuthId,
    val tokenAuthId: AuthId,
    val flowId: FlowId,
    val salt: PasswordSalt,
    val hash: PasswordHash,
    val token: AuthToken
) : Submitted

data class AccountCreateMetaMaskSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val accountId: AccountId,
    val type: AccountType,
    val metamaskAuthId: AuthId,
    val tokenAuthId: AuthId,
    val flowId: FlowId,
    val salt: PasswordSalt,
    val address: Web3Address,
    val token: AuthToken
) : Submitted

data class AccountConvertSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val accountId: AccountId,
    val passwordAuthId: AuthId,
    val tokenAuthId: AuthId,
    val email: Email,
    val hash: PasswordHash,
    val token: AuthToken
) : Submitted