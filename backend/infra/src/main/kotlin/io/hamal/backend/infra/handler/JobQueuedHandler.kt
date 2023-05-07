package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobQueuedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class JobQueuedHandler : HandleDomainNotificationPort<JobQueuedNotification> {
    private val log = logger(JobQueuedHandler::class)
    override fun handle(notification: JobQueuedNotification) {
        log.debug("Handle: $notification")
    }
}