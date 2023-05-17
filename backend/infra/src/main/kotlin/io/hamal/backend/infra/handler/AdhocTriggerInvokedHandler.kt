package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.AdhocTriggerInvokedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort

class AdhocTriggerInvokedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<AdhocTriggerInvokedNotification> {

    private val log = logger(this::class)

    override fun handle(notification: AdhocTriggerInvokedNotification) {
        log.debug("Handle: $notification")
        request(
            JobRequest.PlanJob(
                requestId = RequestId(123),
                shard = notification.shard,
                jobDefinition = notification.invokedTrigger.jobDefinition,
                trigger = notification.invokedTrigger,
            )
        )
    }
}