package io.hamal.backend.event_handler.trigger

import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.trigger.FixedRateTrigger
import io.hamal.backend.service.TriggerInvocationService

class TriggerCreatedHandler(
    val triggerInvocationService: TriggerInvocationService
) : EventHandler<TriggerCreatedEvent> {
    override fun handle(evt: TriggerCreatedEvent) {
        println(evt)
        require(evt.trigger is FixedRateTrigger)
        triggerInvocationService.triggerAdded(evt.trigger)
    }
}