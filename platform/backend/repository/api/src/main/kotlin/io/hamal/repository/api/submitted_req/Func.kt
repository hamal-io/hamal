package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Serializable
data class SubmittedCreateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: FuncId,
    val namespaceId: NamespaceId?,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeValue,
    val codeId: CodeId,
    val codeType: CodeType
) : SubmittedReqWithGroupId


@Serializable
data class SubmittedUpdateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: FuncId,
    val namespaceId: NamespaceId?,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : SubmittedReqWithGroupId
