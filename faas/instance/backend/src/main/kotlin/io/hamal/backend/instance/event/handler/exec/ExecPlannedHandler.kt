package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.events.ExecPlannedEvent
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecPlannedHandler::class)

class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService
) : InstanceEventHandler<ExecPlannedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.schedule(cmdId, evt.plannedExec)
    }
}