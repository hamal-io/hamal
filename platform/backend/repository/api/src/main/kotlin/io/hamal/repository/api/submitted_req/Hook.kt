package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class HookCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val flowId: FlowId,
    val groupId: GroupId,
    val name: HookName,
) : Submitted


@Serializable
data class HookUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val name: HookName?,
) : Submitted

@Serializable
data class HookInvokeSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val invocation: HookInvocation
) : Submitted
