package io.hamal.lib.domain.req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTopicReq(
    val name: TopicName
)

@Serializable
data class SubmittedCreateTopicReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val topicId: TopicId,
    val name: TopicName
) : SubmittedReq


@Serializable
data class AppendEventReq(
    val topicId: TopicId,
    val event: Event
)

@Serializable
data class SubmittedAppendEventReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val topicId: TopicId,
    val event: Event
) : SubmittedReq