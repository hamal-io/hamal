package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.EventWithId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
data class ApiTopicEventList(
    val topicId: TopicId,
    val topicName: TopicName,
    val events: List<EventWithId>
)


@Serializable
data class ApiTopic(
    val id: TopicId,
    val name: TopicName
)


@Serializable
data class ApiTopicList(
    val topics: List<Topic>
) {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}

