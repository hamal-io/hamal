package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionQueuedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.lib.domain.ComputeId
import logger

class ExecQueuedHandler : EventHandler<ExecutionQueuedEvent> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(computeId: ComputeId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}