package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecScheduledNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.infra.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort

class ExecScheduledHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<ExecScheduledNotification> {
    private val log = logger(ExecScheduledHandler::class)
    override fun handle(notification: ExecScheduledNotification) {
        log.debug("Handle: $notification")
        request(
            ExecRequest.QueueScheduledExec(
                reqId = ReqId(1234),
                shard = notification.shard,
                scheduledExec = notification.scheduledExec
            )
        )
    }
}