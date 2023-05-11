package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ManualTriggerInvokedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort

class TriggerInvokedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<ManualTriggerInvokedNotification> {

    private val log = logger(this::class)

    override fun handle(notification: ManualTriggerInvokedNotification) {
        log.debug("Handle: $notification")
        request(
            JobRequest.PlanJob(
                requestId = RequestId(123),
                shard = notification.shard,
                jobDefinition = notification.invokedTrigger.jobDefinition,
                trigger = notification.invokedTrigger.trigger,
            )
        )
    }
}