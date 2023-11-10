package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable


@Serializable
data class FuncCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId,
    val flowId: FlowId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val code: CodeValue
) : Submitted


@Serializable
data class FuncUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : Submitted

@Serializable
data class FuncDeploySubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId,
    val versionToDeploy: CodeVersion
) : Submitted

@Serializable
data class FuncDeployLatestSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId
) : Submitted