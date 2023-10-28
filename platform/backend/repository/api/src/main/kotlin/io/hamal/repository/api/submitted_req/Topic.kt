package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class TopicCreateSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: TopicId,
    val namespaceId: NamespaceId,
    val name: TopicName
) : SubmittedReq


@Serializable
data class TopicAppendToSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: TopicId,
    val payload: TopicEntryPayload
) : SubmittedReq