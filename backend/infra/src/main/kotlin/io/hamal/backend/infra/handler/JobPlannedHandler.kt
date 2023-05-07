package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobPlannedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.RequestId
import io.hamal.lib.ddd.usecase.InvokeRequestOneUseCasePort

class JobPlannedHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<JobPlannedNotification> {
    private val log = logger(this::class)
    override fun handle(notification: JobPlannedNotification) {
        log.debug("Handle: $notification")
        request(
            JobRequest.SchedulePlannedJob(
                shard = notification.shard,
                requestId = RequestId(124),
                plannedJob = notification.plannedJob
            )
        )
    }
}