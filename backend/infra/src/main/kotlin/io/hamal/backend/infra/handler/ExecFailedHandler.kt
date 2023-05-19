package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecutionFailedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class ExecFailedHandler : HandleDomainNotificationPort<ExecutionFailedNotification> {

    private val log = logger(ExecFailedHandler::class)
    override fun handle(notification: ExecutionFailedNotification) {
        log.debug("Handle: $notification")
    }
}