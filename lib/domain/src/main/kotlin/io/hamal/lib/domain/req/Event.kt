package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTopicReq(
    val name: TopicName
)

@Serializable
data class AppendEventReq(
    val topicId: TopicId,
    val payload: EventPayload
)
