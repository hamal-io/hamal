package io.hamal.lib.domain_notification

import kotlin.reflect.KClass

interface DomainNotificationConsumer {

    fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        receiver: DomainNotificationHandler<NOTIFICATION>
    ): DomainNotificationConsumer
}

interface CreateDomainNotificationConsumerPort {
    fun create(): DomainNotificationConsumer
}