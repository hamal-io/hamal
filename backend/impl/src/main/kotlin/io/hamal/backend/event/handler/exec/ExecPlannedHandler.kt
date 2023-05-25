package io.hamal.backend.event.handler.exec

import io.hamal.backend.cmd.ExecCmdService
import io.hamal.backend.event.ExecPlannedEvent
import io.hamal.backend.event.handler.EventHandler
import io.hamal.lib.domain.ReqId
import logger

class ExecPlannedHandler(
    val cmdService: ExecCmdService
) : EventHandler<ExecPlannedEvent> {
    private val log = logger(this::class)
    override fun handle(notification: ExecPlannedEvent) {
        log.debug("Handle: $notification")
        cmdService.schedule(
            ExecCmdService.ToSchedule(
                reqId = ReqId(124),
                plannedExec = notification.plannedExec
            )
        )
    }
}