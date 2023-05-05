package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.trigger.ManualTriggerInvokedNotification
import io.hamal.lib.ddd.port.HandleDomainNotificationPort

class TriggerInvokedHandler : HandleDomainNotificationPort<ManualTriggerInvokedNotification> {
    private val log = logger(TriggerInvokedHandler::class)

    override fun handle(notification: ManualTriggerInvokedNotification) {
        log.info("A trigger was invoked its time to create a job and to have some scheduling fun")
        log.info("$notification")
    }
}