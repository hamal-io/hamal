package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ReqId
import kotlinx.serialization.Serializable

@Serializable
data class TestSubmitted(
    override val reqId: ReqId,
    override var status: ReqStatus
) : Submitted