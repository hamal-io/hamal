package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import kotlinx.serialization.Serializable

@Serializable
data class TestSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus
) : SubmittedReq