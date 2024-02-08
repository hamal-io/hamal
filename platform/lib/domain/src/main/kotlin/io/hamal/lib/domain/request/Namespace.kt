package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

interface FlowCreateRequest {
    val name: FlowName
    val inputs: FlowInputs
    val type: FlowType?
}

data class FlowCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: FlowName,
    val inputs: FlowInputs,
    val flowType: FlowType
) : Requested()

interface FlowUpdateRequest {
    val name: FlowName
    val inputs: FlowInputs
}


data class FlowUpdateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: FlowName,
    val inputs: FlowInputs
) : Requested()
