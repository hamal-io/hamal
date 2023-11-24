package io.hamal.core.req.handler.endpoint

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository.UpdateCmd
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.api.event.EndpointCreatedEvent
import io.hamal.repository.api.submitted_req.EndpointUpdateSubmitted
import org.springframework.stereotype.Component

@Component
class EndpointUpdateHandler(
    val endpointRepository: EndpointRepository, val eventEmitter: PlatformEventEmitter
) : ReqHandler<EndpointUpdateSubmitted>(EndpointUpdateSubmitted::class) {

    override fun invoke(req: EndpointUpdateSubmitted) {
        updateEndpoint(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun EndpointUpdateHandler.updateEndpoint(req: EndpointUpdateSubmitted): Endpoint {
    return endpointRepository.update(
        req.endpointId, UpdateCmd(
            id = req.cmdId(),
            name = req.name,
            method = req.method
        )
    )
}

private fun EndpointUpdateHandler.emitEvent(cmdId: CmdId, endpoint: Endpoint) {
    eventEmitter.emit(cmdId, EndpointCreatedEvent(endpoint))
}
