package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*


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
) : Submitted()

data class FuncUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId,
    val name: FuncName?,
    val inputs: FuncInputs?,
    val code: CodeValue?
) : Submitted()

data class FuncDeploySubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val funcId: FuncId,
    val version: CodeVersion?,
    val message: DeployMessage?
) : Submitted()