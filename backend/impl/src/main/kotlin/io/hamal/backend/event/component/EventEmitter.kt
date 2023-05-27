package io.hamal.backend.event.component

import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.log.ProtobufAppender
import io.hamal.lib.domain.vo.TopicName

class EventEmitter(val brokerRepository: BrokerRepository) {

    private val local: ThreadLocal<List<Pair<Topic, Event>>> = ThreadLocal<List<Pair<Topic, Event>>>()

    private val appender = ProtobufAppender(Event::class, brokerRepository)

    init {
        local.set(listOf())
    }

    fun <EVENT : Event> emit(evt: EVENT) {
        val topic = brokerRepository.resolveTopic(TopicName(evt.topic))
        if (local.get() == null) {
            local.set(listOf(Pair(topic, evt)))
        } else {
            local.set(local.get().plus(Pair(topic, evt)))
        }
        flush()
    }

    fun flush() {
        val notificationsToFlush = local.get() ?: listOf()
        notificationsToFlush.forEach { (topic, evt) -> appender.append(topic, evt) }
        local.remove()
    }
}
