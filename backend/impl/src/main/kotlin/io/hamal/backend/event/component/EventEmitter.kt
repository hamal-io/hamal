package io.hamal.backend.event.component

import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.log.ProtobufAppender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventEmitter
@Autowired constructor(val brokerRepository: BrokerRepository) {

    private val local: ThreadLocal<List<Pair<Topic, Event>>> = ThreadLocal<List<Pair<Topic, Event>>>()

    private val appender = ProtobufAppender(Event::class, brokerRepository)

    init {
        local.set(listOf())
    }

    fun <EVENT : Event> emit(notification: EVENT) {
        val topic = brokerRepository.resolveTopic(Topic.Name(notification.topic))
        if (local.get() == null) {
            local.set(listOf(Pair(topic, notification)))
        } else {
            local.set(local.get().plus(Pair(topic, notification)))
        }
        flush()
    }

    fun flush() {
        val notificationsToFlush = local.get() ?: listOf()
        notificationsToFlush.forEach { (topic, notification) -> appender.append(topic, notification) }
        local.remove()
    }
}
