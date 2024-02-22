package io.hamal.core.request.handler.endpoint

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.EndpointCreateRequested
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.event.EndpointCreatedEvent
import org.springframework.stereotype.Component

@Component
class EndpointCreateHandler(
    val endpointCmdRepository: EndpointCmdRepository,
    val eventEmitter: InternalEventEmitter,
    val funcQueryRepository: FuncQueryRepository
) : RequestHandler<EndpointCreateRequested>(EndpointCreateRequested::class) {
    override fun invoke(req: EndpointCreateRequested) {
        createEndpoint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun EndpointCreateHandler.createEndpoint(req: EndpointCreateRequested): Endpoint {
    val func = funcQueryRepository.get(req.funcId)
    return endpointCmdRepository.create(
        CreateCmd(
            id = req.cmdId(),
            endpointId = req.endpointId,
            workspaceId = req.workspaceId,
            funcId = func.id,
            namespaceId = func.namespaceId,
            name = req.name
        )
    )
}

private fun EndpointCreateHandler.emitEvent(cmdId: CmdId, endpoint: Endpoint) {
    eventEmitter.emit(cmdId, EndpointCreatedEvent(endpoint))
}
