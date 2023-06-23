package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecPlannedEvent
import io.hamal.backend.event.handler.SystemEventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.CmdId
import logger

class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService
) : SystemEventHandler<ExecPlannedEvent> {
    private val log = logger(this::class)
    override fun handle(cmdId: CmdId, evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.schedule(cmdId, evt.plannedExec)
    }
}