package io.hamal.backend.instance.req.handler.state

import io.hamal.backend.instance.event.StateUpdatedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.ReqHandler
import io.hamal.backend.instance.req.handler.cmdId
import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.submitted_req.SubmittedSetStateReq
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import org.springframework.stereotype.Component

@Component
class SetStateHandler(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: SystemEventEmitter
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
