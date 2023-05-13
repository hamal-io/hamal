package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.JobCompletedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class JobCompletedHandler : HandleDomainNotificationPort<JobCompletedNotification> {

    private val log = logger(JobCompletedHandler::class)
    override fun handle(notification: JobCompletedNotification) {
        log.debug("Handle: $notification")
    }
}