package io.hamal.backend.service.cmd

import io.hamal.backend.event.Event
import io.hamal.backend.event.TenantEvent
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.log.ProtobufAppender
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.stereotype.Service

@Service
class EventCmdService(
    val brokerRepository: BrokerRepository
) {

    private val appender = ProtobufAppender(Event::class, brokerRepository)

    fun create(toCreate: TopicToCreate): Topic {
        return brokerRepository.resolveTopic(
            toCreate.name
        )
    }

    fun append(eventToAppend: EventToAppend) {
        val topic = brokerRepository.get(eventToAppend.topicId)
        appender.append(
            topic, TenantEvent(
                shard = eventToAppend.shard,
                topic = "tenant-topic",
                contentType = eventToAppend.contentTpe,
                value = eventToAppend.value
            )
        )

    }

    data class TopicToCreate(
        val reqId: ReqId,
        val shard: Shard,
        val tenantId: TenantId,
        val name: TopicName
    )

    data class EventToAppend(
        val reqId: ReqId,
        val shard: Shard,

        val tenantId: TenantId,
        val topicId: TopicId,
        val contentTpe: String,
        val value: ByteArray

    )
}