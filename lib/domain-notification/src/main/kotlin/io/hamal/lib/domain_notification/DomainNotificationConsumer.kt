package io.hamal.lib.domain_notification

import io.hamal.lib.ddd.base.DomainNotification

interface DomainNotificationConsumer {
    fun poll(topic: String): DomainNotification?
}