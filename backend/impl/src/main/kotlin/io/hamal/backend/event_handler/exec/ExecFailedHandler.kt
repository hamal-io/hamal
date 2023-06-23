package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionFailedEvent
import io.hamal.backend.event_handler.SystemEventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.CmdId
import logger

class ExecFailedHandler(
    private val orchestrationService: OrchestrationService
) : SystemEventHandler<ExecutionFailedEvent> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(cmdId: CmdId, evt: ExecutionFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(cmdId, evt.failedExec)
    }
}