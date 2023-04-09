package io.hamal.lib.domain_notification

import kotlin.reflect.KClass

interface HandleDomainNotificationPort {

    fun <NOTIFICATION : DomainNotification> register(
        clazz: KClass<NOTIFICATION>,
        receiver: DomainNotificationHandler<NOTIFICATION>
    ): HandleDomainNotificationPort

}