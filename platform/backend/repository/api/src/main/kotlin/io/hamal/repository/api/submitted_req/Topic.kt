package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class TopicCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val topicId: TopicId,
    val flowId: FlowId,
    val name: TopicName
) : Submitted


@Serializable
data class TopicAppendToSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val topicId: TopicId,
    val groupId: GroupId,
    val payload: TopicEntryPayload
) : Submitted