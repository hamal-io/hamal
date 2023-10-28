package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class AccountCreateWithPasswordSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: AccountId,
    val type: AccountType,
    val authenticationId: AuthId,
    val namespaceId: NamespaceId,
    val name: AccountName,
    val email: AccountEmail?,
    val salt: PasswordSalt,
    val hash: PasswordHash,
    val token: AuthToken
) : Submitted