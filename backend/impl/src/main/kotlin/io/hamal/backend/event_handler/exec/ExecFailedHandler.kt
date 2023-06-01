package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionFailedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.ComputeId
import logger

class ExecFailedHandler(
    private val orchestrationService: OrchestrationService
) : EventHandler<ExecutionFailedEvent> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(computeId: ComputeId, evt: ExecutionFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(computeId, evt.failedExec)
    }
}