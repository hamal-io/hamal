package io.hamal.core.req.handler.endpoint

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.event.EndpointCreatedEvent
import io.hamal.repository.api.submitted_req.EndpointCreateSubmitted
import org.springframework.stereotype.Component

@Component
class EndpointCreateHandler(
    val endpointCmdRepository: EndpointCmdRepository,
    val eventEmitter: PlatformEventEmitter,
    val funcQueryRepository: FuncQueryRepository
) : ReqHandler<EndpointCreateSubmitted>(EndpointCreateSubmitted::class) {
    override fun invoke(req: EndpointCreateSubmitted) {
        createEndpoint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun EndpointCreateHandler.createEndpoint(req: EndpointCreateSubmitted): Endpoint {
    val func = funcQueryRepository.get(req.funcId)
    return endpointCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            endpointId = req.endpointId,
            groupId = req.groupId,
            funcId = func.id,
            flowId = func.flowId,
            name = req.name,
            method = req.method,
        )
    )
}

private fun EndpointCreateHandler.emitEvent(cmdId: CmdId, endpoint: Endpoint) {
    eventEmitter.emit(cmdId, EndpointCreatedEvent(endpoint))
}
