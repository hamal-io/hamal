package io.hamal.core.req.handler.state

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.submitted.StateSetSubmitted
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.event.StateUpdatedEvent
import org.springframework.stereotype.Component

@Component
class StateSetHandler(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<StateSetSubmitted>(StateSetSubmitted::class) {
    override fun invoke(req: StateSetSubmitted) {
        updateState(req).also { emitEvent(req.cmdId(), req.state) }
    }
}

private fun StateSetHandler.updateState(req: StateSetSubmitted) {
    return stateCmdRepository.set(req.cmdId(), req.state)
}

private fun StateSetHandler.emitEvent(cmdId: CmdId, state: CorrelatedState) {
    eventEmitter.emit(cmdId, StateUpdatedEvent(state))
}
