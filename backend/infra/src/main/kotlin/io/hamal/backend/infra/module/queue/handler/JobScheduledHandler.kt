package io.hamal.backend.infra.module.queue.handler

import io.hamal.backend.core.logger
import io.hamal.backend.notification.JobScheduledNotification
import io.hamal.backend.notification.port.HandleDomainNotificationPort
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.RequestId
import io.hamal.lib.ddd.usecase.InvokeRequestOneUseCasePort

class JobScheduledHandler(
    val request: InvokeRequestOneUseCasePort
) : HandleDomainNotificationPort<JobScheduledNotification> {
    private val log = logger(JobScheduledHandler::class)
    override fun handle(notification: JobScheduledNotification) {
        log.debug("Handle: $notification")
        request(
            JobRequest.QueueScheduledJob(
                requestId = RequestId(1234),
                shard = notification.shard,
                scheduledJob = notification.scheduledJob
            )
        )
    }
}