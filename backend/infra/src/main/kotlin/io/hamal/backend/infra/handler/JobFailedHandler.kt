package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobFailedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class JobFailedHandler : HandleDomainNotificationPort<JobFailedNotification> {

    private val log = logger(JobFailedHandler::class)
    override fun handle(notification: JobFailedNotification) {
        log.debug("Handle: $notification")
    }
}