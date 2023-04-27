package io.hamal.backend.core.port.notification

import io.hamal.backend.core.notification.DomainNotification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}
