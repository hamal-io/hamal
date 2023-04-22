package io.hamal.lib.domain_notification

import io.hamal.lib.domain_notification.notification.DomainNotification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}