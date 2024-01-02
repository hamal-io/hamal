package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class FlowCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: FlowName,
    val inputs: FlowInputs,
    val type: FlowType
) : Submitted

data class FlowUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: FlowName,
    val inputs: FlowInputs
) : Submitted
