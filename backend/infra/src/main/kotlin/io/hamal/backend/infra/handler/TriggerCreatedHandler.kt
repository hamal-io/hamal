package io.hamal.backend.infra.handler

import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import io.hamal.backend.infra.DummyDb

class TriggerCreatedHandler : HandleDomainNotificationPort<TriggerDomainNotification.Created> {
    override fun handle(notification: TriggerDomainNotification.Created) {
        println("==================================")
        println(notification)
        println(notification.trigger.id.partition())
        println(notification.trigger.id.sequence())
        println(notification.trigger.id.elapsed())
        DummyDb.triggers[notification.trigger.id] = notification.trigger
    }
}