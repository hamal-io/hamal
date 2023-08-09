package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
sealed interface SubmittedReq {
    val reqId: ReqId
    var status: ReqStatus
}

