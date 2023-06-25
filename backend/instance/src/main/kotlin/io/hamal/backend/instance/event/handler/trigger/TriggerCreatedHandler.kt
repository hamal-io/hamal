package io.hamal.backend.instance.event.handler.trigger

import io.hamal.backend.instance.event.TriggerCreatedEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.instance.service.FixedRateTriggerService
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.FixedRateTrigger

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