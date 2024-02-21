package io.hamal.core.adapter.topic

import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import org.springframework.stereotype.Component

fun interface TopicEventListPort {
    operator fun invoke(query: TopicEventQuery): List<TopicEvent>
}

@Component
class TopicEventListAdapter(
    private val topicQueryRepository: TopicQueryRepository
) : TopicEventListPort {
    override fun invoke(query: TopicEventQuery): List<TopicEvent> = topicQueryRepository.list(query)
}

