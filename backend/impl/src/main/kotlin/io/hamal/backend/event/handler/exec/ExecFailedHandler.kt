package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecutionFailedEvent
import io.hamal.backend.event.handler.EventHandler
import logger

class ExecFailedHandler : EventHandler<ExecutionFailedEvent> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(notification: ExecutionFailedEvent) {
        log.debug("Handle: $notification")
    }
}