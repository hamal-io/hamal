package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecScheduledEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.CommandId
import logger

class ExecScheduledHandler(
    val cmdService: ExecCmdService
) : EventHandler<ExecScheduledEvent> {
    private val log = logger(ExecScheduledHandler::class)
    override fun handle(commandId: CommandId, evt: ExecScheduledEvent) {
        log.debug("Handle: $evt")
        cmdService.enqueue(commandId, evt.scheduledExec)
    }
}