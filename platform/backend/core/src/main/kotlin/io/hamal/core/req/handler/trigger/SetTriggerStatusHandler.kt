package io.hamal.core.req.handler.trigger

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerCmdRepository
import io.hamal.repository.api.event.TriggerActivatedEvent
import io.hamal.repository.api.event.TriggerDeactivatedEvent
import io.hamal.repository.api.submitted_req.TriggerStatusSubmitted
import org.springframework.stereotype.Component

@Component
class SetTriggerStatusHandler(
    val triggerCmdRepository: TriggerCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<TriggerStatusSubmitted>(TriggerStatusSubmitted::class) {
    override fun invoke(req: TriggerStatusSubmitted) {
        setStatus(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun SetTriggerStatusHandler.setStatus(req: TriggerStatusSubmitted): Trigger {
    return triggerCmdRepository.setStatus(
        req.triggerId, TriggerCmdRepository.SetTriggerStatusCmd(
            id = req.cmdId(),
            status = req.triggerStatus
        )
    )
}

private fun SetTriggerStatusHandler.emitEvent(cmdId: CmdId, trigger: Trigger) {
    if (trigger.status == TriggerStatus.Active) {
        eventEmitter.emit(cmdId, TriggerActivatedEvent(trigger))
    } else {
        eventEmitter.emit(cmdId, TriggerDeactivatedEvent(trigger))
    }
}