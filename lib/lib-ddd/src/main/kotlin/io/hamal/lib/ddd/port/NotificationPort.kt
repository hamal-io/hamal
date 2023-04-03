package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.DomainNotification

interface NotificationPort<NOTIFICATION : DomainNotification> {
    fun send(notification: NOTIFICATION)
}