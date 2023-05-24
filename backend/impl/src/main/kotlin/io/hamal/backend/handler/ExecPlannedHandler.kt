package io.hamal.backend.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.notification.ExecPlannedNotification
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import logger

class ExecPlannedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<ExecPlannedNotification> {
    private val log = logger(this::class)
    override fun handle(notification: ExecPlannedNotification) {
        log.debug("Handle: $notification")
        request(
            ExecRequest.SchedulePlannedExec(
                shard = notification.shard,
                reqId = ReqId(124),
                plannedExec = notification.plannedExec
            )
        )
    }
}