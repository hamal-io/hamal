package io.hamal.backend.adapter

import io.hamal.backend.core.notification.port.FlushDomainNotificationPort
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.notification.DomainNotification
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.log.ProtobufAppender


class DomainNotificationAdapter(
    val brokerRepository: BrokerRepository,
) : NotifyDomainPort, FlushDomainNotificationPort {

    private val local: ThreadLocal<List<Pair<Topic, DomainNotification>>> =
        ThreadLocal<List<Pair<Topic, DomainNotification>>>()

    private val appender = ProtobufAppender(DomainNotification::class, brokerRepository)

    init {
        local.set(listOf())
    }

    override fun <NOTIFICATION : DomainNotification> invoke(notification: NOTIFICATION) {
        val topic = brokerRepository.resolveTopic(Topic.Name(notification.topic))
        if (local.get() == null) {
            local.set(listOf(Pair(topic, notification)))
        } else {
            local.set(local.get().plus(Pair(topic, notification)))
        }
    }

    override fun invoke() {
        val notificationsToFlush = local.get() ?: listOf()
        notificationsToFlush.forEach { (topic, notification) -> appender.append(topic, notification) }
        local.remove()
    }
}
