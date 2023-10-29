package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val authId: AuthId,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val hash: PasswordHash,
    val token: AuthToken
) : Submitted