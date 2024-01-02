package io.hamal.core.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.event.PlatformEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.ProtobufAppender

class PlatformEventEmitter(
    private val generateDomainId: GenerateId,
    private val brokerRepository: BrokerRepository
) {
    fun <EVENT : PlatformEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(FlowId.root, evt.topicName) ?: brokerRepository.create(
            cmdId,
            TopicToCreate(generateDomainId(::TopicId), evt.topicName, FlowId.root, GroupId.root)
        )
        appender.append(cmdId, topic, evt)
    }

    private val appender = ProtobufAppender(PlatformEvent::class, brokerRepository)
}
