package io.hamal.backend.instance.event.handler.trigger

import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.events.TriggerCreatedEvent
import io.hamal.backend.instance.service.FixedRateTriggerService
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.lib.common.domain.CmdId

class TriggerCreatedHandler(
    private val fixedRateTriggerService: FixedRateTriggerService
) : InstanceEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        if (evt.trigger is FixedRateTrigger) {
            fixedRateTriggerService.triggerAdded(evt.trigger)
        }
    }
}