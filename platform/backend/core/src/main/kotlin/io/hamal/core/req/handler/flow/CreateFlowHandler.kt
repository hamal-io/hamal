package io.hamal.core.req.handler.flow

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.event.FlowCreatedEvent
import io.hamal.repository.api.submitted_req.FlowCreateSubmitted
import org.springframework.stereotype.Component

@Component
class CreateFlowHandler(
    val flowCmdRepository: FlowCmdRepository,
    val flowQueryRepository: FlowQueryRepository,
    val eventEmitter: PlatformEventEmitter,
    val generateDomainId: GenerateDomainId
) : ReqHandler<FlowCreateSubmitted>(FlowCreateSubmitted::class) {

    /**
     * Creates new flows on a best-effort basis. Might throw an exception if used concurrently
     */
    override fun invoke(req: FlowCreateSubmitted) {
        createFlow(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun CreateFlowHandler.createFlow(req: FlowCreateSubmitted): Flow {
    return flowCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            flowId = req.flowId,
            groupId = req.groupId,
            type = req.type,
            name = req.name,
            inputs = req.inputs
        )
    )
}

private fun CreateFlowHandler.emitEvent(cmdId: CmdId, flow: Flow) {
    eventEmitter.emit(cmdId, FlowCreatedEvent(flow))
}
