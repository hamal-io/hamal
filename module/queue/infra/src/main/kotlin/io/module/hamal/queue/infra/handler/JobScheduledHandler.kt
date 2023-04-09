package io.module.hamal.queue.infra.handler

import io.hamal.lib.domain_notification.DomainNotificationHandler
import io.hamal.lib.domain_notification.notification.JobDomainNotification.Scheduled

class JobScheduledHandler : DomainNotificationHandler<Scheduled> {
    override fun on(notification: Scheduled) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}