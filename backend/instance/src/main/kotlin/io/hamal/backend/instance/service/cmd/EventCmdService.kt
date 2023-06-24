package io.hamal.backend.instance.service.cmd

import io.hamal.backend.instance.event.Event
import io.hamal.backend.repository.api.log.CreateTopic
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.backend.repository.api.log.ProtobufAppender
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.springframework.stereotype.Service

@Service
class EventCmdService<TOPIC : LogTopic>(
    private val eventBrokerRepository: LogBrokerRepository<TOPIC>
) {

    private val appender = ProtobufAppender(Event::class, eventBrokerRepository)

    fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic {
        return eventBrokerRepository.create(
            cmdId,
            CreateTopic.TopicToCreate(
                id = toCreate.id,
                name = toCreate.name
            )
        )
    }

    fun append(cmdId: CmdId, eventToAppend: EventToAppend) {
        val topic = eventBrokerRepository.get(eventToAppend.topicId)
        appender.append(
            cmdId, topic, Event(
                contentType = eventToAppend.contentTpe,
                content = eventToAppend.content
            )
        )
    }

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )

    data class EventToAppend(
        val topicId: TopicId,
        val contentTpe: ContentType,
        val content: Content
    )
}