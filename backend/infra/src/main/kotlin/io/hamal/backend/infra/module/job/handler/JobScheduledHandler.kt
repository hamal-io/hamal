package io.hamal.backend.infra.module.job.handler

import io.hamal.backend.notification.JobScheduledNotification
import io.hamal.backend.notification.port.HandleDomainNotificationPort

class JobScheduledHandler : HandleDomainNotificationPort<JobScheduledNotification> {
    override fun handle(notification: JobScheduledNotification) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}