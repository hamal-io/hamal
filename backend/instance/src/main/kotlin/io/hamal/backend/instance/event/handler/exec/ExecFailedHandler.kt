package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecutionFailedEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.instance.service.OrchestrationService
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