package io.hamal.backend.service.cmd

import io.hamal.backend.event.Event
import io.hamal.backend.event.TenantEvent
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.sqlite.log.ProtobufAppender
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.stereotype.Service

@Service
class EventCmdService(
    val logBrokerRepository: LogBrokerRepository
) {

    private val appender = ProtobufAppender(Event::class, logBrokerRepository)

    fun create(toCreate: TopicToCreate): LogTopic {
        return logBrokerRepository.resolveTopic(
            toCreate.name
        )
    }

    fun append(commandId: CommandId, eventToAppend: EventToAppend) {
        val topic = logBrokerRepository.get(eventToAppend.topicId)
        appender.append(
            commandId, topic, TenantEvent(
//                commandId = eventToAppend.commandId,
                shard = eventToAppend.shard,
                topic = "tenant-topic",
                contentType = eventToAppend.contentTpe,
                value = eventToAppend.value
            )
        )

    }

    data class TopicToCreate(
        val commandId: CommandId,
        val shard: Shard,
        val accountId: AccountId,
        val name: TopicName
    )

    data class EventToAppend(
        val commandId: CommandId,
        val shard: Shard,

        val accountId: AccountId,
        val topicId: TopicId,
        val contentTpe: String,
        val value: ByteArray

    )
}