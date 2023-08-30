package io.hamal.backend.instance.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.ProtobufAppender

class InstanceEventEmitter(
    private val generateDomainId: GenerateDomainId,
    private val brokerRepository: LogBrokerRepository
) {

    private val appender = ProtobufAppender(SystemEvent::class, brokerRepository)

    fun <EVENT : SystemEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(evt.topic) ?: brokerRepository.create(
            cmdId,
            CreateTopic.TopicToCreate(generateDomainId(::TopicId), evt.topic)
        )

        appender.append(cmdId, topic, evt)
    }
}
