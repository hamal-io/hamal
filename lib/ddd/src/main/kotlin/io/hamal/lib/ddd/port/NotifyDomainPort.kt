package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.DomainNotification

interface NotifyDomainPort<NOTIFICATION : DomainNotification> {
    operator fun invoke(notification: NOTIFICATION)
}