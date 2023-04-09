package io.hamal.lib.domain_notification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}