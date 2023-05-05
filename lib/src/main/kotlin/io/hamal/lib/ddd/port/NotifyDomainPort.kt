package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.DomainNotification

interface NotifyDomainPort {
    operator fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION)
}
