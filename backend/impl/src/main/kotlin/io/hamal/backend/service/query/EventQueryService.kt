package io.hamal.backend.service.query

import io.hamal.backend.event.Event
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Topic
import io.hamal.lib.domain.vo.TopicId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.stereotype.Service

@Service
class EventQueryService(
    val brokerRepository: BrokerRepository
) {

    fun queryTopics(block: TopicQuery.() -> Unit): List<Topic> {
        val query = TopicQuery()
        block(query)
        //FIXME apply query filter

        return brokerRepository.topics().toList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun queryEvents(query: EventQuery): List<Event> {
        val topic = brokerRepository.get(query.topicId)
        return brokerRepository.read(Chunk.Id(0), topic, query.limit)
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