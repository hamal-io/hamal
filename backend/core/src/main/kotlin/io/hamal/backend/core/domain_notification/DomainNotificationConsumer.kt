package io.hamal.backend.core.domain_notification

import io.hamal.backend.core.domain_notification.notification.DomainNotification
import kotlin.reflect.KClass

interface DomainNotificationConsumer {
    fun cancel()
}

interface CreateDomainNotificationConsumerPort {

    fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        handler: DomainNotificationHandler<NOTIFICATION>
    ): CreateDomainNotificationConsumerPort

    fun create(): DomainNotificationConsumer
}