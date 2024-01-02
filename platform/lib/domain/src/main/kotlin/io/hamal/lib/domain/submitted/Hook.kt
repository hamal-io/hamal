package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class HookCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val flowId: FlowId,
    val groupId: GroupId,
    val name: HookName,
) : Submitted()


data class HookUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val name: HookName?,
) : Submitted()

data class HookInvokeSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val hookId: HookId,
    val groupId: GroupId,
    val invocation: HookInvocation
) : Submitted()
