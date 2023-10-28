package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Serializable
data class FuncCreateSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: FuncId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val code: CodeValue
) : SubmittedReq


@Serializable
data class FuncUpdateSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: FuncId,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : SubmittedReq
