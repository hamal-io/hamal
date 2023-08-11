package io.hamal.backend.repository.api.submitted_req

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedSetStateReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val state: CorrelatedState
) : SubmittedReq
