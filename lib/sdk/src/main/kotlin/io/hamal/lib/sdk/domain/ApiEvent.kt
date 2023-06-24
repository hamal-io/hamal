package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("do not have separate dto")
data class ApiCreateTopicRequest(
    val name: TopicName
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiCreateTopicResponse(
    val id: TopicId,
    val name: TopicName
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiAppendEvenRequest(
    val value: String
)


@Serializable
@Deprecated("do not have separate dto")
data class ApiAppendEventResponse(
    // FIXME
    val topicId: TopicId,
    val topicName: TopicName
)


@Serializable
@Deprecated("do not have separate dto")
data class ApiListEventResponse(
    val topicId: TopicId,
    val topicName: TopicName,
    val events: List<Event>
) {

    @Serializable
    data class Event(
        val contentType: String,
        val value: String
    )
}


@Serializable
data class ListTopicsResponse(
    val topics: List<Topic>
) {
    @Serializable
    data class Topic(
        val id: TopicId,
        val name: TopicName
    )
}


