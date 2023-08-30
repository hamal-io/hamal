package io.hamal.backend.instance.req.handler.state

import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.events.StateUpdatedEvent
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedSetStateReq
import org.springframework.stereotype.Component

@Component
class SetStateHandler(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: InstanceEventEmitter
) : ReqHandler<SubmittedSetStateReq>(SubmittedSetStateReq::class) {
    override fun invoke(req: SubmittedSetStateReq) {
        updateState(req).also { emitEvent(req.cmdId(), req.state) }
    }
}

private fun SetStateHandler.updateState(req: SubmittedSetStateReq) {
    return stateCmdRepository.set(req.cmdId(), req.state)
}

private fun SetStateHandler.emitEvent(cmdId: CmdId, state: CorrelatedState) {
    eventEmitter.emit(cmdId, StateUpdatedEvent(state))
}
