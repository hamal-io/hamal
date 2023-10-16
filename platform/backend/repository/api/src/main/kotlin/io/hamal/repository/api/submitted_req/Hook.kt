package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedCreateHookReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: HookId,
    val namespaceId: NamespaceId?,
    val name: HookName,
) : SubmittedReqWithGroupId


@Serializable
data class SubmittedUpdateHookReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: HookId,
    val namespaceId: NamespaceId?,
    val name: HookName?,
) : SubmittedReqWithGroupId

@Serializable
data class SubmittedInvokeHookReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: HookId,
    val execId: ExecId,
    val headers: HookHeaders,
    val parameters: HookParameters
) : SubmittedReq
