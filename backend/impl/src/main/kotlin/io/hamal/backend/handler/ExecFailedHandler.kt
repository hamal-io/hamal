package io.hamal.backend.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.notification.ExecutionFailedNotification
import logger

class ExecFailedHandler : HandleDomainNotificationPort<ExecutionFailedNotification> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(notification: ExecutionFailedNotification) {
        log.debug("Handle: $notification")
    }
}