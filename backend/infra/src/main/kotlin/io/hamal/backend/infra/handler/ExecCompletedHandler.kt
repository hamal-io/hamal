package io.hamal.backend.infra.handler

import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecutionCompletedNotification
import io.hamal.backend.core.notification.port.HandleDomainNotificationPort

class ExecCompletedHandler : HandleDomainNotificationPort<ExecutionCompletedNotification> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(notification: ExecutionCompletedNotification) {
        log.debug("Handle: $notification")
    }
}