package io.hamal.core.event.handler.trigger

import io.hamal.core.event.HubEventHandler
import io.hamal.core.service.FixedRateTriggerService
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.event.TriggerCreatedEvent

internal class TriggerCreatedHandler(
    private val fixedRateTriggerService: FixedRateTriggerService
) : HubEventHandler<TriggerCreatedEvent> {
    override fun handle(cmdId: CmdId, evt: TriggerCreatedEvent) {
        val trigger = evt.trigger
        if (trigger is FixedRateTrigger) {
            fixedRateTriggerService.triggerAdded(trigger)
        }
    }
}