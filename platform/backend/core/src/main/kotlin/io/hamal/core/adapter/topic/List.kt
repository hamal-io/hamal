package io.hamal.core.adapter.topic

import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.stereotype.Component

fun interface TopicListPort {
    operator fun invoke(query: TopicQuery): List<Topic>
}

@Component
class TopicListAdapter(
    private val topicQueryRepository: TopicQueryRepository
) : TopicListPort {
    override fun invoke(query: TopicQuery): List<Topic> = topicQueryRepository.list(query)
}