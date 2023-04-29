package io.hamal.backend.infra.module.trigger.handler

import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import io.hamal.backend.infra.adapter.BackendLoggingAdapter

class TriggerInvokedHandler : HandleDomainNotificationPort<TriggerDomainNotification.Invoked> {
    companion object {
        val log = BackendLoggingAdapter(TriggerInvokedHandler::class)
    }

    override fun handle(notification: TriggerDomainNotification.Invoked) {
        log.info("A trigger was invoked its time to create a flow and to have some scheduling fun")
        log.info("$notification")
    }
}