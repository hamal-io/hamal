package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.GroupId
import kotlinx.serialization.Serializable

@Serializable
data class StateSetSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val state: CorrelatedState
) : SubmittedReqWithGroupId
