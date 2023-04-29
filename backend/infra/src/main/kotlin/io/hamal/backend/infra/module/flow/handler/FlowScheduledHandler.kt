package io.hamal.backend.infra.module.flow.handler

import io.hamal.backend.core.notification.Scheduled
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort

class FlowScheduledHandler : HandleDomainNotificationPort<Scheduled> {
    override fun handle(notification: Scheduled) {
        println("${Thread.currentThread().name} QUEUE received ${notification.inputs}")
    }
}