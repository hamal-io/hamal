package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*


interface TopicGroupCreateRequest {
    val name: TopicName
}

interface TopicPublicCreateRequest {
    val name: TopicName
}

data class TopicGroupCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val logTopicId: LogTopicId,
    val groupId: GroupId,
    val name: TopicName
) : Requested()


data class TopicPublicCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val logTopicId: LogTopicId,
    val groupId: GroupId,
    val name: TopicName
) : Requested()

interface TopicAppendEntryRequest {
    val topicId: TopicId
    val payload: TopicEventPayload
}

data class TopicAppendEventRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val payload: TopicEventPayload
) : Requested()