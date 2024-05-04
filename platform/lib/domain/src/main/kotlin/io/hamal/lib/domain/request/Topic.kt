package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TopicTypes
import io.hamal.lib.domain.vo.*


interface TopicCreateRequest {
    val name: TopicName
    val type: TopicTypes
}

data class TopicCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: TopicId,
    val logTopicId: LogTopicId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId,
    val name: TopicName,
    val type: TopicTypes
) : Requested()


interface TopicAppendEntryRequest {
    val topicId: TopicId
    val payload: TopicEventPayload
}

data class TopicAppendEventRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: TopicId,
    val payload: TopicEventPayload
) : Requested()