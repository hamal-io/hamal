package io.hamal.backend.event_handler.trigger

import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event_handler.SystemEventHandler
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.service.FixedRateTriggerService
import io.hamal.lib.domain.CmdId

class TriggerCreatedHandler(
    val fixedRateTriggerService: FixedRateTriggerService
) : SystemEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        println(evt)
        if (evt.trigger is FixedRateTrigger) {
            fixedRateTriggerService.triggerAdded(evt.trigger)
        }
    }
}