package io.hamal.backend.repository.api.submitted_req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedCreateTopicReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: TopicId,
    val name: TopicName
) : SubmittedReq


@Serializable
data class SubmittedAppendToTopicReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: TopicId,
    val event: Event
) : SubmittedReq