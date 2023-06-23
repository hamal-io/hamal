package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecScheduledEvent
import io.hamal.backend.event.handler.SystemEventHandler
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.CmdId
import logger

class ExecScheduledHandler(
    val cmdService: ExecCmdService
) : SystemEventHandler<ExecScheduledEvent> {
    private val log = logger(ExecScheduledHandler::class)
    override fun handle(cmdId: CmdId, evt: ExecScheduledEvent) {
        log.debug("Handle: $evt")
        cmdService.queue(cmdId, evt.scheduledExec)
    }
}