package io.hamal.core.request.handler.trigger

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.request.TriggerStatusRequested
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.event.TriggerActivatedEvent
import io.hamal.repository.api.event.TriggerDeactivatedEvent
import org.springframework.stereotype.Component

@Component
class TriggerSetStatusHandler(
    private val triggerCmdRepository: TriggerCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<TriggerStatusRequested>(TriggerStatusRequested::class) {

    override fun invoke(req: TriggerStatusRequested) {
        setStatus(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun setStatus(req: TriggerStatusRequested): Trigger {
        return triggerCmdRepository.set(
            req.triggerId, TriggerCmdRepository.SetTriggerStatusCmd(
                id = req.cmdId(),
                status = req.triggerStatus
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, trigger: Trigger) {
        if (trigger.status == TriggerStatus.Active) {
            eventEmitter.emit(cmdId, TriggerActivatedEvent(trigger))
        } else {
            eventEmitter.emit(cmdId, TriggerDeactivatedEvent(trigger))
        }
    }
}
