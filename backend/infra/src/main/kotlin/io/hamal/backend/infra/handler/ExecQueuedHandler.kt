package io.hamal.backend.infra.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.infra.notification.ExecutionQueuedNotification
import logger

class ExecQueuedHandler : HandleDomainNotificationPort<ExecutionQueuedNotification> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(notification: ExecutionQueuedNotification) {
        log.debug("Handle: $notification")
    }
}