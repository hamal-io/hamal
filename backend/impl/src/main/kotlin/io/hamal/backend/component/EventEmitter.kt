package io.hamal.backend.component

import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicName

class EventEmitter<TOPIC : LogTopic>(private val brokerRepository: LogBrokerRepository<TOPIC>) {

    private val local: ThreadLocal<List<Pair<TOPIC, Event>>> = ThreadLocal<List<Pair<TOPIC, Event>>>()

    private val appender = ProtobufAppender(Event::class, brokerRepository)

    init {
        local.set(listOf())
    }

    fun <EVENT : Event> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.resolveTopic(TopicName(evt.topic))
        if (local.get() == null) {
            local.set(listOf(Pair(topic, evt)))
        } else {
            local.set(local.get().plus(Pair(topic, evt)))
        }
        flush(cmdId)
    }


    fun flush(cmdId: CmdId) {
        val notificationsToFlush = local.get() ?: listOf()
        notificationsToFlush.forEach { (topic, evt) -> appender.append(cmdId, topic, evt) }
        local.remove()
    }
}
