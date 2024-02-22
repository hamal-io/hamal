package io.hamal.core.request.handler.state

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.request.StateSetRequested
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.event.StateUpdatedEvent
import org.springframework.stereotype.Component

@Component
class StateSetHandler(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: InternalEventEmitter
) : RequestHandler<StateSetRequested>(StateSetRequested::class) {
    override fun invoke(req: StateSetRequested) {
        updateState(req).also { emitEvent(req.cmdId(), req.state) }
    }
}

private fun StateSetHandler.updateState(req: StateSetRequested) {
    return stateCmdRepository.set(req.cmdId(), req.state)
}

private fun StateSetHandler.emitEvent(cmdId: CmdId, state: CorrelatedState) {
    eventEmitter.emit(cmdId, StateUpdatedEvent(state))
}
