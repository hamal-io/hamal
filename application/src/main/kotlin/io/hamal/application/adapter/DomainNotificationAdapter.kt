package io.hamal.application.adapter

import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.ddd.port.NotifyDomainPort
import org.springframework.stereotype.Component

@Component
class DomainNotificationAdapter<NOTIFICATION : DomainNotification> : NotifyDomainPort<NOTIFICATION> {
    override fun invoke(notification: NOTIFICATION) {
        println(notification)
    }
}