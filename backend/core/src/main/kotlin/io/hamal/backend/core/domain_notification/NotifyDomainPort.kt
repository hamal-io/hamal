package io.hamal.backend.core.domain_notification

import io.hamal.backend.core.domain_notification.notification.DomainNotification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}
