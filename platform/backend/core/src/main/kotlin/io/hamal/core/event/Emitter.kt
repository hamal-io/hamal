package io.hamal.core.event

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.event.PlatformEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.repository.api.log.ProtobufAppender

class PlatformEventEmitter(
    private val generateDomainId: GenerateDomainId,
    private val brokerRepository: BrokerRepository
) {

    private val appender = ProtobufAppender(PlatformEvent::class, brokerRepository)

    fun <EVENT : PlatformEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(GroupId.root, evt.topicName) ?: brokerRepository.create(
            cmdId,
            TopicToCreate(generateDomainId(::TopicId), evt.topicName, GroupId.root)
        )

        appender.append(cmdId, topic, evt)
    }
}
