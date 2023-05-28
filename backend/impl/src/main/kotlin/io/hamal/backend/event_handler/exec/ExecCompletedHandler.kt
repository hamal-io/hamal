package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.ReqId
import logger

class ExecCompletedHandler(
    val orchestrationService: OrchestrationService
) : EventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(
            reqId = ReqId(123),
            completedExec = evt.completedExec
        )
    }
}