package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.events.ExecutionFailedEvent
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecFailedHandler::class)

class ExecFailedHandler(
    private val orchestrationService: OrchestrationService
) : InstanceEventHandler<ExecutionFailedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(cmdId, evt.failedExec)
    }
}