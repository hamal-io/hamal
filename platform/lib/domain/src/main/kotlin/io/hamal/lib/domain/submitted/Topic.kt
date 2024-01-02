package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class TopicCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val topicId: TopicId,
    val flowId: FlowId,
    val name: TopicName
) : Submitted()


data class TopicAppendToSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val topicId: TopicId,
    val groupId: GroupId,
    val payload: TopicEntryPayload
) : Submitted()