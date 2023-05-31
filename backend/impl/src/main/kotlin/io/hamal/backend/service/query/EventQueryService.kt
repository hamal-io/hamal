package io.hamal.backend.service.query

import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.domain.vo.TopicId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.stereotype.Service

@Service
class EventQueryService(
    val logBrokerRepository: LogBrokerRepository
) {

    fun queryTopics(block: TopicQuery.() -> Unit): List<LogTopic> {
        val query = TopicQuery()
        block(query)
        //FIXME apply query filter

        return logBrokerRepository.topics().toList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun queryEvents(query: EventQuery): List<Event> {
        val topic = logBrokerRepository.get(query.topicId)
        return logBrokerRepository.read(LogChunkId(0), topic, query.limit)
            .map { chunk ->
                ProtoBuf.decodeFromByteArray(Event.serializer(), chunk.bytes)
            }
    }


    data class TopicQuery(
        val afterId: TopicId = TopicId(0),
        val limit: Int = 100 //FIXME VO
    )

    data class EventQuery(
        val topicId: TopicId,
//        val afterId: EventId = TopicId(0),
        val limit: Int = 100 //FIXME VO
    )
}