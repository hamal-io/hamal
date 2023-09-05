package io.hamal.core.event.handler.exec

import io.hamal.core.event.HubEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.event.ExecutionFailedEvent
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