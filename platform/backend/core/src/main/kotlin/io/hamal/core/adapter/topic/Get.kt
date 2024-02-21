package io.hamal.core.adapter.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository
import org.springframework.stereotype.Component

fun interface TopicGetPort {
    operator fun invoke(topicId: TopicId): Topic
}

@Component
class TopicGetAdapter(
    private val topicQueryRepository: TopicQueryRepository
) : TopicGetPort {
    override fun invoke(topicId: TopicId): Topic = topicQueryRepository.get(topicId)
}
