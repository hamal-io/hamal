package io.hamal.backend.core.notification.port

import io.hamal.backend.notification.DomainNotification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}
