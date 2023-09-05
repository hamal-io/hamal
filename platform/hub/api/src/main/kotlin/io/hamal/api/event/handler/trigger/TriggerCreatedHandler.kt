package io.hamal.api.event.handler.trigger

import io.hamal.api.event.HubEventHandler
import io.hamal.api.service.FixedRateTriggerService
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.event.TriggerCreatedEvent

class TriggerCreatedHandler(
    private val fixedRateTriggerService: FixedRateTriggerService
) : HubEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        val trigger = evt.trigger
        if (trigger is FixedRateTrigger) {
            fixedRateTriggerService.triggerAdded(trigger)
        }
    }
}