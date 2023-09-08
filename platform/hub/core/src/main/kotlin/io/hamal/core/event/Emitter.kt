package io.hamal.core.event

import io.hamal.core.service.MetricService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.event.HubEvent
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.CreateTopic
import io.hamal.repository.api.log.ProtobufAppender

class HubEventEmitter(
    private val generateDomainId: GenerateDomainId,
    private val brokerRepository: BrokerRepository,
    private val metricService: MetricService
) {

    private val appender = ProtobufAppender(HubEvent::class, brokerRepository)

    fun <EVENT : HubEvent> emit(cmdId: CmdId, evt: EVENT) {
        val topic = brokerRepository.findTopic(evt.topicName) ?: brokerRepository.create(
            cmdId,
            CreateTopic.TopicToCreate(generateDomainId(::TopicId), evt.topicName)
        )

        appender.append(cmdId, topic, evt)
        metricService.handleEvent(evt)
    }
}
