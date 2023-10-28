package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ExtensionSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExtensionId,
    val name: ExtensionName,
    val codeId: CodeId,
    val code: CodeValue
) : SubmittedReqWithGroupId

@Serializable
data class ExtensionSubmittedUpdateReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: ExtensionId,
    val name: ExtensionName?,
    val code: CodeValue?
) : SubmittedReqWithGroupId