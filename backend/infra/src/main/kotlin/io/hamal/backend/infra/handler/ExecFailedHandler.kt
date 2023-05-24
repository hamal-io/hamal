package io.hamal.backend.infra.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.infra.notification.ExecutionFailedNotification
import logger

class ExecFailedHandler : HandleDomainNotificationPort<ExecutionFailedNotification> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(notification: ExecutionFailedNotification) {
        log.debug("Handle: $notification")
    }
}