package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.TenantId
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
    val tenantId: TenantId,
    val name: TopicName
) : Req
