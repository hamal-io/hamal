package io.hamal.backend.infra.module.job.handler

import io.hamal.backend.core.job.JobScheduledNotification
import io.hamal.lib.ddd.port.HandleDomainNotificationPort

class JobScheduledHandler : HandleDomainNotificationPort<JobScheduledNotification> {
    override fun handle(notification: JobScheduledNotification) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}