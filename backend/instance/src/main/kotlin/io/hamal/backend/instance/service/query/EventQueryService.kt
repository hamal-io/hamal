package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventWithId
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.domain.vo.TopicId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.stereotype.Service

@Service
class EventQueryService<TOPIC : LogTopic>(
    private val eventBrokerRepository: LogBrokerRepository<TOPIC>
) {

    fun getTopic(topicId: TopicId) = eventBrokerRepository.find(topicId)
        ?: throw NoSuchElementException("Topic not found")

    fun findTopic(topicId: TopicId) = eventBrokerRepository.find(topicId)

    fun queryTopics(block: TopicQuery.() -> Unit): List<TOPIC> {
        val query = TopicQuery().also(block)
        //FIXME apply query filter
        return eventBrokerRepository.queryTopics()
            .toList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun queryEvents(topic: TOPIC, block: EventQuery.() -> Unit): List<EventWithId> {
        val query = EventQuery().also(block)
        val firstId = LogChunkId(SnowflakeId(query.afterId.value.value + 1))
        return eventBrokerRepository.read(firstId, topic, query.limit.value)
            .map { chunk ->
                val evt = ProtoBuf.decodeFromByteArray(Event.serializer(), chunk.bytes)
                EventWithId(
                    id = EventId(chunk.id.value),
                    contentType = evt.contentType,
                    content = evt.content
                )
            }
    }


    data class TopicQuery(
        var afterId: TopicId = TopicId(0),
        var limit: Limit = Limit(1)
    )

    data class EventQuery(
        var afterId: EventId = EventId(0),
        var limit: Limit = Limit(1)
    )
}