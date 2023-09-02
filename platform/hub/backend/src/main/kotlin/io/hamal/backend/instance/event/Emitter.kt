package io.hamal.backend.instance.event

import io.hamal.backend.instance.event.event.InstanceEvent
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.ProtobufAppender

class InstanceEventEmitter(
    private val generateDomainId: GenerateDomainId,
    private val brokerRepository: BrokerRepository
) {

    private val appender = ProtobufAppender(InstanceEvent::class, brokerRepository)

    fun <EVENT : InstanceEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(evt.topicName) ?: brokerRepository.create(
            cmdId,
            CreateTopic.TopicToCreate(generateDomainId(::TopicId), evt.topicName)
        )

        appender.append(cmdId, topic, evt)
    }
}
