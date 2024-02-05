package io.hamal.core.request.handler.flow

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.FlowUpdateRequested
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository
import io.hamal.repository.api.event.FlowCreatedEvent
import org.springframework.stereotype.Component


@Component
class FlowUpdateHandler(
    val flowCmdRepository: FlowCmdRepository,
    val eventEmitter: InternalEventEmitter
) : io.hamal.core.request.RequestHandler<FlowUpdateRequested>(FlowUpdateRequested::class) {

    override fun invoke(req: FlowUpdateRequested) {
        updateFlow(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun FlowUpdateHandler.updateFlow(req: FlowUpdateRequested): Flow {
    return flowCmdRepository.update(
        req.flowId,
        FlowCmdRepository.UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            inputs = req.inputs,
        )
    )
}

private fun FlowUpdateHandler.emitEvent(cmdId: CmdId, flow: Flow) {
    eventEmitter.emit(cmdId, FlowCreatedEvent(flow))
}
