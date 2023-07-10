package io.hamal.backend.instance.event

import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.port.GenerateDomainId

class SystemEventEmitter<TOPIC : LogTopic>(
    private val generateDomainId: GenerateDomainId,
    private val brokerRepository: LogBrokerRepository<TOPIC>
) {

    private val local: ThreadLocal<List<Pair<TOPIC, SystemEvent>>> = ThreadLocal<List<Pair<TOPIC, SystemEvent>>>()

    private val appender = ProtobufAppender(SystemEvent::class, brokerRepository)

    init {
        local.set(listOf())
    }

    fun <EVENT : SystemEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(evt.topic) ?: brokerRepository.create(
            cmdId,
            CreateTopic.TopicToCreate(generateDomainId(::TopicId), evt.topic)
        )
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
