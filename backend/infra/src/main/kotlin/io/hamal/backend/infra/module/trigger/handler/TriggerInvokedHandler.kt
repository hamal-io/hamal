package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.logger
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort

class TriggerInvokedHandler : HandleDomainNotificationPort<TriggerDomainNotification.Invoked> {
    private val log = logger(TriggerInvokedHandler::class)

    override fun handle(notification: TriggerDomainNotification.Invoked) {
        log.info("A trigger was invoked its time to create a job and to have some scheduling fun")
        log.info("$notification")
    }
}