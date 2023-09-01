package io.hamal.lib.domain.req

import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTopicReq(
    val name: TopicName
)

@Serializable
data class AppendEntryReq(
    val topicId: TopicId,
    val payload: TopicEntryPayload
)
