package io.hamal.backend.notification.port

import io.hamal.backend.notification.DomainNotification
import kotlin.reflect.KClass

interface HandleDomainNotificationPort<out NOTIFICATION : DomainNotification> {

    fun handle(notification: @UnsafeVariance NOTIFICATION)

    interface Container {

        fun <NOTIFICATION : DomainNotification> register(
            clazz: KClass<NOTIFICATION>,
            receiver: HandleDomainNotificationPort<NOTIFICATION>
        ): Boolean

        operator fun <NOTIFICATION : DomainNotification> get(clazz: KClass<NOTIFICATION>): List<HandleDomainNotificationPort<NOTIFICATION>>

        fun topics(): Set<String>;

    }
}


