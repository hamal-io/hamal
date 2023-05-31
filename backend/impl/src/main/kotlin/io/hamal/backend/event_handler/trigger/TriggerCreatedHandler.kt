package io.hamal.backend.event_handler.trigger

import io.hamal.backend.event.TriggerCreatedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.service.TriggerInvocationService
import io.hamal.lib.domain.ReqId

class TriggerCreatedHandler(
    val triggerInvocationService: TriggerInvocationService
) : EventHandler<TriggerCreatedEvent> {
    override fun handle(reqId: ReqId, evt: TriggerCreatedEvent) {
        println(evt)
        require(evt.trigger is FixedRateTrigger)
        triggerInvocationService.triggerAdded(evt.trigger)
    }
}