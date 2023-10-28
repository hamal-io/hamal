package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class TopicCreateSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: TopicId,
    val namespaceId: NamespaceId,
    val name: TopicName
) : Submitted


@Serializable
data class TopicAppendToSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val id: TopicId,
    val payload: TopicEntryPayload
) : Submitted