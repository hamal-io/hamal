package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*


interface TopicCreateRequest {
    val name: TopicName
    val type: TopicType
}

data class TopicCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val logTopicId: LogTopicId,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: TopicName,
    val type: TopicType
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