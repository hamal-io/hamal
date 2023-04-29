package io.hamal.backend.core.port.flow

import io.hamal.backend.core.model.Flow.PlannedFlow
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.FlowId

fun interface PlanFlowPort {
    fun planFlow(flowToPlan: FlowToPlan): PlannedFlow
    data class FlowToPlan(
        val flowId: FlowId,
        val definitionId: FlowDefinitionId
    )
}