package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable


@Serializable
data class SubmittedCreateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: FuncId,
    val groupId: GroupId,
    val namespaceId: NamespaceId?,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
) : SubmittedReq


@Serializable
data class SubmittedUpdateFuncReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: FuncId,
    val namespaceId: NamespaceId?,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeType?
) : SubmittedReq
