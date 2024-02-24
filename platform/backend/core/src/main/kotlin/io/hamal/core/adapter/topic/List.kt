package io.hamal.core.adapter.topic

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.stereotype.Component

fun interface TopicListPort {
    operator fun invoke(query: TopicQuery): List<Topic>
}

@Component
class TopicListAdapter(
    private val topicQueryRepository: TopicQueryRepository,
    private val filterAccess: FilterAccessPort
) : TopicListPort {
    override fun invoke(query: TopicQuery): List<Topic> = topicQueryRepository.list(query).mapNotNull { topic ->
        if (topic is Topic.Public) {
            topic
        } else {
            filterAccess(topic)
        }
    }
}