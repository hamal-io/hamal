package io.hamal.backend.infra.handler

import io.hamal.backend.core.notification.Scheduled
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort

class JobScheduledHandler : HandleDomainNotificationPort<Scheduled> {
    override fun handle(notification: Scheduled) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}