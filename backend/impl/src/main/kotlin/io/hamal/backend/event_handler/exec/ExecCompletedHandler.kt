package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.ComputeId
import logger

class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService
) : EventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(computeId: ComputeId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(computeId, evt.completedExec)
    }
}