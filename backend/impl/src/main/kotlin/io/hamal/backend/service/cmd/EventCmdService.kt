package io.hamal.backend.service.cmd

import io.hamal.backend.event.TenantEvent
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.common.Partition
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.stereotype.Service

@Service
class EventCmdService<TOPIC : LogTopic>(
    private val logBrokerRepository: LogBrokerRepository<TOPIC>
) {

    private val appender = ProtobufAppender(TenantEvent::class, logBrokerRepository)

    fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic {
        TODO()
//        return logBrokerRepository.create(
//            cmdId,
//            CreateTopic.TopicToCreate(
//                id = toCreate.id,
//                name = toCreate.name
//            )
//        )
    }

    fun append(cmdId: CmdId, eventToAppend: EventToAppend) {
        val topic = logBrokerRepository.get(eventToAppend.topicId)
        appender.append(
            cmdId, topic, TenantEvent(
                contentType = eventToAppend.contentTpe,
                value = eventToAppend.value
            )
        )
    }

    data class TopicToCreate(
        val id: TopicId,
        val tenantId: TenantId,
        val name: TopicName
    )

    data class EventToAppend(
        val cmdId: CmdId,
        val partition: Partition,

        val tenantId: TenantId,
        val topicId: TopicId,
        val contentTpe: String,
        val value: ByteArray

    )
}