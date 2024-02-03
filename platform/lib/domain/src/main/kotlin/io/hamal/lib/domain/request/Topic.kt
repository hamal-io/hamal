package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*


interface TopicCreateRequest {
    val name: TopicName
}

data class TopicFlowCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val logTopicId: LogTopicId,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: TopicName
) : Requested()


interface TopicAppendEntryRequest {
    val topicId: TopicId
    val payload: TopicEntryPayload
}

data class TopicAppendToRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val topicId: TopicId,
    val groupId: GroupId,
    val payload: TopicEntryPayload
) : Requested()