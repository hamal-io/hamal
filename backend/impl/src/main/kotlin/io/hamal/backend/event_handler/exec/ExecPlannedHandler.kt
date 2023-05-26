package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecPlannedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId
import logger

class ExecPlannedHandler(
    private val cmdService: ExecCmdService
) : EventHandler<ExecPlannedEvent> {
    private val log = logger(this::class)
    override fun handle(evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        cmdService.schedule(
            ExecCmdService.ToSchedule(
                reqId = ReqId(124),
                plannedExec = evt.plannedExec
            )
        )
    }
}