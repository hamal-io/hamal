package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import kotlinx.serialization.Serializable

@Serializable
sealed interface SubmittedReq {
    val id: ReqId
    var status: ReqStatus
}

enum class ReqStatus {
    Submitted,
    Completed,
    Failed;
}
