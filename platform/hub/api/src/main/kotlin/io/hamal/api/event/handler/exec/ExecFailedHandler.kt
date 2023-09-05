package io.hamal.api.event.handler.exec

import io.hamal.api.event.HubEventHandler
import io.hamal.repository.api.event.ExecutionFailedEvent
import io.hamal.api.service.OrchestrationService
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