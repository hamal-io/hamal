package io.hamal.backend.event.handler.exec

import io.hamal.backend.cmd.ExecCmdService
import io.hamal.backend.event.ExecScheduledEvent
import io.hamal.backend.event.handler.EventHandler
import io.hamal.lib.domain.ReqId
import logger

class ExecScheduledHandler(
    val cmdService: ExecCmdService
) : EventHandler<ExecScheduledEvent> {
    private val log = logger(ExecScheduledHandler::class)
    override fun handle(notification: ExecScheduledEvent) {
        log.debug("Handle: $notification")
        cmdService.enqueue(
            ExecCmdService.ToEnqueue(
                reqId = ReqId(1234),
                scheduledExec = notification.scheduledExec
            )
        )
    }
}