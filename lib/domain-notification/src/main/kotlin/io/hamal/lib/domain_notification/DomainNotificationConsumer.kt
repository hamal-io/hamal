package io.hamal.lib.domain_notification

import kotlin.reflect.KClass

interface DomainNotificationConsumer {
    fun poll(topic: String): DomainNotification?
}

fun <NOTIFICATION : DomainNotification> KClass<NOTIFICATION>.topic(): String {
    return this.annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }.let {
        it as DomainNotificationTopic
    }.value
}