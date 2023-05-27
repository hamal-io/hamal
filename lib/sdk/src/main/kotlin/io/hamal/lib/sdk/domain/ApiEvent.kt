package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable


@Serializable
data class ApiAppendEvenRequest(
    val data: String
)


@Serializable
data class ApiListEventResponse(
    val topicId: TopicId,
    val topicName: TopicName,
    val events: List<Event>
) {

    @Serializable
    data class Event(
        val data: String
    )
}


@Serializable
data class ApiListTopicResponse(
    val topics: List<Topic>
) {

    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


