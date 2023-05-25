package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionQueuedEvent
import io.hamal.backend.event_handler.EventHandler
import logger

class ExecQueuedHandler : EventHandler<ExecutionQueuedEvent> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(notification: ExecutionQueuedEvent) {
        log.debug("Handle: $notification")
    }
}