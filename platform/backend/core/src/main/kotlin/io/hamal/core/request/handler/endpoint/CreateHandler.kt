package io.hamal.core.request.handler.endpoint

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.EndpointCreateRequested
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.event.EndpointCreatedEvent
import org.springframework.stereotype.Component

@Component
class EndpointCreateHandler(
    private val endpointCmdRepository: EndpointCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val funcGet: FuncGetPort
) : RequestHandler<EndpointCreateRequested>(EndpointCreateRequested::class) {

    override fun invoke(req: EndpointCreateRequested) {
        createEndpoint(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createEndpoint(req: EndpointCreateRequested): Endpoint {
        val func = funcGet(req.funcId)
        return endpointCmdRepository.create(
            CreateCmd(
                id = req.cmdId(),
                endpointId = req.id,
                workspaceId = req.workspaceId,
                funcId = func.id,
                namespaceId = func.namespaceId,
                name = req.name
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, endpoint: Endpoint) {
        eventEmitter.emit(cmdId, EndpointCreatedEvent(endpoint))
    }
}

