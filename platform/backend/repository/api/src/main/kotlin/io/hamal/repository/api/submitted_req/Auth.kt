package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class AuthLoginEmailSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val authId: AuthId,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val defaultFlowIds: Map<GroupId, FlowId>,
    val hash: PasswordHash,
    val token: AuthToken,
) : Submitted

data class AuthLoginMetaMaskSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val authId: AuthId,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val defaultFlowIds: Map<GroupId, FlowId>,
    val token: AuthToken,
    val address: Web3Address,
    val signature: Web3Signature
) : Submitted


data class AuthLogoutSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val accountId: AccountId
) : Submitted