package io.hamal.backend.core.port.flow

import io.hamal.backend.core.model.Flow.CompletedFlow
import io.hamal.lib.vo.FlowId

fun interface CompleteFlowPort {
    fun completeFlow(flowId: FlowId): CompletedFlow
}