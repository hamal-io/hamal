package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event.handler.SystemEventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.CmdId
import logger

class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService
) : SystemEventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
    }
}