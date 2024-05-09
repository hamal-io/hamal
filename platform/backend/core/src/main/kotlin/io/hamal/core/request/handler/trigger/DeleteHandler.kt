package io.hamal.core.request.handler.trigger

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.TriggerDeleteRequested
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.event.TriggerDeletedEvent
import org.springframework.stereotype.Component

@Component
internal class TriggerDeleteHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<TriggerDeleteRequested>(TriggerDeleteRequested::class) {

    override fun invoke(req: TriggerDeleteRequested) {
        val trigger = triggerQueryRepository.get(req.id)

        triggerCmdRepository.delete(
            TriggerCmdRepository.DeleteCmd(
                id = req.cmdId(),
                triggerId = req.id
            )
        )
        emitEvent(req.cmdId(), trigger)
    }

    private fun emitEvent(cmdId: CmdId, trigger: Trigger) {
        eventEmitter.emit(cmdId, TriggerDeletedEvent(trigger))
    }

}