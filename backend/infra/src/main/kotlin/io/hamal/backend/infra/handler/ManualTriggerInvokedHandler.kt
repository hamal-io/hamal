package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ManualTriggerInvokedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort

class ManualTriggerInvokedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<ManualTriggerInvokedNotification> {

    private val log = logger(this::class)

    override fun handle(notification: ManualTriggerInvokedNotification) {
        log.debug("Handle: $notification")
        request(
            ExecRequest.PlanExec(
                reqId = ReqId(123),
                shard = notification.shard,
                func = notification.cause.func,
                trigger = notification.cause,
            )
        )
    }
}