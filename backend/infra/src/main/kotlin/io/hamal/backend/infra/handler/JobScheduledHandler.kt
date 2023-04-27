package io.hamal.backend.infra.handler

import io.hamal.lib.domain_notification.DomainNotificationHandler
import io.hamal.lib.domain_notification.notification.Scheduled

class JobScheduledHandler : DomainNotificationHandler<Scheduled> {
    override fun on(notification: Scheduled) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}