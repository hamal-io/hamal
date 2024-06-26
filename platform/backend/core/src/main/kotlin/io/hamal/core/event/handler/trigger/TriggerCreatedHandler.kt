package io.hamal.core.event.handler.trigger

import io.hamal.core.event.InternalEventHandler
import io.hamal.core.service.FixedRateTriggerService
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.event.TriggerCreatedEvent
import org.springframework.stereotype.Component

@Component
internal class TriggerCreatedHandler(
    private val fixedRateTriggerService: FixedRateTriggerService
) : InternalEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        val trigger = evt.trigger
        if (trigger is Trigger.FixedRate) {
            fixedRateTriggerService.triggerAdded(trigger)
        }
    }
}