package io.hamal.backend.infra.module.queue.handler

import io.hamal.backend.core.logger
import io.hamal.backend.notification.JobQueuedNotification
import io.hamal.backend.notification.port.HandleDomainNotificationPort

class JobQueuedHandler : HandleDomainNotificationPort<JobQueuedNotification> {
    private val log = logger(JobQueuedHandler::class)
    override fun handle(notification: JobQueuedNotification) {
        log.debug("Handle: $notification")
    }
}