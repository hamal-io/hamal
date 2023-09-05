package io.hamal.api.event.handler.exec

import io.hamal.api.event.HubEventHandler
import io.hamal.repository.api.event.ExecPlannedEvent
import io.hamal.api.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecPlannedHandler::class)

class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService
) : HubEventHandler<ExecPlannedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.schedule(cmdId, evt.plannedExec)
    }
}