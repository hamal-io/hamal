package io.hamal.lib.domain.req

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import kotlinx.serialization.Serializable

@Serializable
data class SetStateReq(
    val correlation: Correlation,
    val value: State
)

@Serializable
data class SubmittedSetStateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val state: CorrelatedState
) : SubmittedReq
