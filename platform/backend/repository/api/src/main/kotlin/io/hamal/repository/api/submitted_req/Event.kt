package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedCreateTopicReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: TopicId,
    val namespaceId: NamespaceId,
    val name: TopicName
) : SubmittedReqWithGroupId


@Serializable
data class SubmittedAppendToTopicReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val id: TopicId,
    val payload: TopicEntryPayload
) : SubmittedReqWithGroupId