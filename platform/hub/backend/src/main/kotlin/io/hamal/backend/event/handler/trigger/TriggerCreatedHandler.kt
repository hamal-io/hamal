package io.hamal.backend.event.handler.trigger

import io.hamal.backend.event.HubEventHandler
import io.hamal.backend.event.event.TriggerCreatedEvent
import io.hamal.backend.service.FixedRateTriggerService
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.lib.common.domain.CmdId

class TriggerCreatedHandler(
    private val fixedRateTriggerService: FixedRateTriggerService
) : HubEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        if (evt.trigger is FixedRateTrigger) {
            fixedRateTriggerService.triggerAdded(evt.trigger)
        }
    }
}