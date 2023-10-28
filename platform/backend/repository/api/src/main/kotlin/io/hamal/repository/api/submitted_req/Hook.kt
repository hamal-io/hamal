package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class HookCreateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: HookId,
    val namespaceId: NamespaceId,
    val groupId: GroupId,
    val name: HookName,
) : Submitted


@Serializable
data class HookUpdateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: HookId,
    val groupId: GroupId,
    val name: HookName?,
) : Submitted

@Serializable
data class HookInvokeSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: HookId,
    val groupId: GroupId,
    val method: HookMethod,
    val headers: HookHeaders,
    val parameters: HookParameters
) : Submitted
