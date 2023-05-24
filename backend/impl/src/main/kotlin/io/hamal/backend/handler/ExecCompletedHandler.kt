package io.hamal.backend.handler

import io.hamal.backend.core.notification.port.HandleDomainNotificationPort
import io.hamal.backend.notification.ExecutionCompletedNotification
import logger

class ExecCompletedHandler : HandleDomainNotificationPort<ExecutionCompletedNotification> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(notification: ExecutionCompletedNotification) {
        log.debug("Handle: $notification")
    }
}