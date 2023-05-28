package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecPlannedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.ReqId
import logger

class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService
) : EventHandler<ExecPlannedEvent> {
    private val log = logger(this::class)
    override fun handle(evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.schedule(
            reqId = ReqId(124),
            plannedExec = evt.plannedExec
        )
    }
}