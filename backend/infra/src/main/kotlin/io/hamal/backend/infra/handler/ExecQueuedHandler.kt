package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecutionQueuedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class ExecQueuedHandler : HandleDomainNotificationPort<ExecutionQueuedNotification> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(notification: ExecutionQueuedNotification) {
        log.debug("Handle: $notification")
    }
}