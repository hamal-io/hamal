package io.hamal.core.req.handler.flow

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.request.FlowCreateRequested
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.event.FlowCreatedEvent
import org.springframework.stereotype.Component


@Component
class FlowCreateHandler(
    val flowCmdRepository: FlowCmdRepository,
    val flowQueryRepository: FlowQueryRepository,
    val eventEmitter: PlatformEventEmitter,
    val generateDomainId: GenerateId
) : ReqHandler<FlowCreateRequested>(FlowCreateRequested::class) {

    /**
     * Creates new flows on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: FlowCreateRequested) {
        createFlow(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun FlowCreateHandler.createFlow(req: FlowCreateRequested): Flow {
    return flowCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            flowId = req.flowId,
            groupId = req.groupId,
            type = req.flowType,
            name = req.name,
            inputs = req.inputs
        )
    )
}

private fun FlowCreateHandler.emitEvent(cmdId: CmdId, flow: Flow) {
    eventEmitter.emit(cmdId, FlowCreatedEvent(flow))
}
