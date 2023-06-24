package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecPlannedEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.instance.service.OrchestrationService
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