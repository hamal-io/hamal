package io.hamal.core.req.handler.state

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.event.StateUpdatedEvent
import io.hamal.repository.api.submitted_req.StateSetSubmitted
import org.springframework.stereotype.Component

@Component
class SetStateHandler(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<StateSetSubmitted>(StateSetSubmitted::class) {
    override fun invoke(req: StateSetSubmitted) {
        updateState(req).also { emitEvent(req.cmdId(), req.state) }
    }
}

private fun SetStateHandler.updateState(req: StateSetSubmitted) {
    return stateCmdRepository.set(req.cmdId(), req.state)
}

private fun SetStateHandler.emitEvent(cmdId: CmdId, state: CorrelatedState) {
    eventEmitter.emit(cmdId, StateUpdatedEvent(state))
}
