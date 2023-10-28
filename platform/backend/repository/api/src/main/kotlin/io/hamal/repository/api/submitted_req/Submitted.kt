package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
sealed interface SubmittedReq {
    val reqId: ReqId
    var status: ReqStatus
}