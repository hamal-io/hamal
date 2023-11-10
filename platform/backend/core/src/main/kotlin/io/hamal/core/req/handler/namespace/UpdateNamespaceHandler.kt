package io.hamal.core.req.handler.flow

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository
import io.hamal.repository.api.event.FlowCreatedEvent
import io.hamal.repository.api.submitted_req.FlowUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class UpdateFlowHandler(
    val flowCmdRepository: FlowCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FlowUpdateSubmitted>(FlowUpdateSubmitted::class) {

    override fun invoke(req: FlowUpdateSubmitted) {
        updateFlow(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun UpdateFlowHandler.updateFlow(req: FlowUpdateSubmitted): Flow {
    return flowCmdRepository.update(
        req.flowId,
        FlowCmdRepository.UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
        )
    )
}

private fun UpdateFlowHandler.emitEvent(cmdId: CmdId, flow: Flow) {
    eventEmitter.emit(cmdId, FlowCreatedEvent(flow))
}
