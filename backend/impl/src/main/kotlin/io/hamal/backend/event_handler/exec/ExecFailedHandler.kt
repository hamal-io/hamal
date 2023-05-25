package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionFailedEvent
import io.hamal.backend.event_handler.EventHandler
import logger

class ExecFailedHandler : EventHandler<ExecutionFailedEvent> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(notification: ExecutionFailedEvent) {
        log.debug("Handle: $notification")
    }
}