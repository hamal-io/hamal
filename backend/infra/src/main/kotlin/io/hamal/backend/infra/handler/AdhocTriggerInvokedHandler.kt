package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.AdhocTriggerInvokedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.ExecRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort

class AdhocTriggerInvokedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<AdhocTriggerInvokedNotification> {

    private val log = logger(this::class)

    override fun handle(notification: AdhocTriggerInvokedNotification) {
        log.debug("Handle: $notification")
        request(
            ExecRequest.PlanExec(
                reqId = ReqId(123),
                shard = notification.shard,
                trigger = notification.adhocTrigger,
                code =notification.adhocTrigger.code
            )
        )
    }
}