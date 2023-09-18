package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.GroupId
import kotlinx.serialization.Serializable

@Serializable
sealed interface SubmittedReq {
    val reqId: ReqId
    var status: ReqStatus
}

@Serializable
sealed interface SubmittedReqWithGroupId : SubmittedReq {
    override val reqId: ReqId
    override var status: ReqStatus
    val groupId: GroupId
}
