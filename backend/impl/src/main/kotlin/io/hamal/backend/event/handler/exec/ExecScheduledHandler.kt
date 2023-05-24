package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.ExecScheduledEvent
import io.hamal.backend.event.handler.EventHandler
import io.hamal.backend.cmd.ExecCmd
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import logger

class ExecScheduledHandler(
    val request: InvokeRequestOneUseCasePort
) : EventHandler<ExecScheduledEvent> {
    private val log = logger(ExecScheduledHandler::class)
    override fun handle(notification: ExecScheduledEvent) {
        log.debug("Handle: $notification")
        request(
            ExecCmd.QueueScheduledExec(
                reqId = ReqId(1234),
                shard = notification.shard,
                scheduledExec = notification.scheduledExec
            )
        )
    }
}