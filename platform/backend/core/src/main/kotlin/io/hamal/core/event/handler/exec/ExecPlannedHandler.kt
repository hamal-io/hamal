package io.hamal.core.event.handler.exec

import io.hamal.core.event.InternalEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.event.ExecPlannedEvent

private val log = logger(ExecPlannedHandler::class)

internal class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService
) : InternalEventHandler<ExecPlannedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.schedule(cmdId, evt.plannedExec)
    }
}