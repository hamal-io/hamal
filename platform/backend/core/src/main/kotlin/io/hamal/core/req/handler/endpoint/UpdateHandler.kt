package io.hamal.core.req.handler.endpoint

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.EndpointUpdateRequested
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository.UpdateCmd
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.api.event.EndpointCreatedEvent
import org.springframework.stereotype.Component


@Component
class EndpointUpdateHandler(
    val endpointRepository: EndpointRepository, val eventEmitter: PlatformEventEmitter
) : ReqHandler<EndpointUpdateRequested>(EndpointUpdateRequested::class) {

    override fun invoke(req: EndpointUpdateRequested) {
        updateEndpoint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun EndpointUpdateHandler.updateEndpoint(req: EndpointUpdateRequested): Endpoint {
    return endpointRepository.update(
        req.endpointId, UpdateCmd(
            id = req.cmdId(),
            funcId = req.funcId,
            name = req.name
        )
    )
}

private fun EndpointUpdateHandler.emitEvent(cmdId: CmdId, endpoint: Endpoint) {
    eventEmitter.emit(cmdId, EndpointCreatedEvent(endpoint))
}
