package io.hamal.backend.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.notification.ExecutionQueuedNotification
import logger

class ExecQueuedHandler : HandleDomainNotificationPort<ExecutionQueuedNotification> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(notification: ExecutionQueuedNotification) {
        log.debug("Handle: $notification")
    }
}