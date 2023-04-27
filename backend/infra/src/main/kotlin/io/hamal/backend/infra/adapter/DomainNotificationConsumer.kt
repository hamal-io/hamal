package io.hamal.backend.infra.adapter

import io.hamal.backend.core.notification.DomainNotification
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import kotlin.reflect.KClass

interface DomainNotificationConsumer {
    fun cancel()
}

interface CreateDomainNotificationConsumerPort {

    fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: HandleDomainNotificationPort<NOTIFICATION>
    ): CreateDomainNotificationConsumerPort

    fun create(): DomainNotificationConsumer
}