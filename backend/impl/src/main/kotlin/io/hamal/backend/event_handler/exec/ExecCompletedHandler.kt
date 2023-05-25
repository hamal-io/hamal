package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event_handler.EventHandler
import logger

class ExecCompletedHandler : EventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(notification: ExecutionCompletedEvent) {
        log.debug("Handle: $notification")
    }
}