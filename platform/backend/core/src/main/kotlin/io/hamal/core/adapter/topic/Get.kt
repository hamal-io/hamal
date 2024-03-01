package io.hamal.core.adapter.topic

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository
import org.springframework.stereotype.Component

fun interface TopicGetPort {
    operator fun invoke(topicId: TopicId): Topic
}

@Component
class TopicGetAdapter(
    private val topicQueryRepository: TopicQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : TopicGetPort {
    override fun invoke(topicId: TopicId): Topic = when (val result = topicQueryRepository.get(topicId)) {
        is Topic.Public -> result
        else -> ensureAccess(result)
    }
}
