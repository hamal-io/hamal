package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionQueuedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.lib.domain.CommandId
import logger

class ExecQueuedHandler : EventHandler<ExecutionQueuedEvent> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(commandId: CommandId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}