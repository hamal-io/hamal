package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionFailedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.backend.service.cmd.ReqCmdService
import io.hamal.lib.domain.ReqId
import logger

class ExecFailedHandler(
    private val orchestrationService: OrchestrationService,
    private val reqCmdService: ReqCmdService
) : EventHandler<ExecutionFailedEvent> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(reqId: ReqId, evt: ExecutionFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(reqId, evt.failedExec)
        reqCmdService.fail(reqId)
    }
}