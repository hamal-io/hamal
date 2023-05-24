package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecPlannedNotification
import io.hamal.backend.event.handler.EventHandler
import io.hamal.backend.cmd.ExecCmd
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import logger

class ExecPlannedHandler(
    val request: InvokeRequestOneUseCasePort
) : EventHandler<ExecPlannedNotification> {
    private val log = logger(this::class)
    override fun handle(notification: ExecPlannedNotification) {
        log.debug("Handle: $notification")
        request(
            ExecCmd.SchedulePlannedExec(
                shard = notification.shard,
                reqId = ReqId(124),
                plannedExec = notification.plannedExec
            )
        )
    }
}