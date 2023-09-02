package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.HubEventHandler
import io.hamal.backend.event.event.ExecutionFailedEvent
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecFailedHandler::class)

class ExecFailedHandler(
    private val orchestrationService: OrchestrationService
) : HubEventHandler<ExecutionFailedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(cmdId, evt.failedExec)
    }
}