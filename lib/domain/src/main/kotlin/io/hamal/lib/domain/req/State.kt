package io.hamal.lib.domain.req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.Serializable

@Serializable
data class SetStateReq(
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val state: State
)

@Serializable
data class SubmittedSetStateReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val funcId: FuncId,
    val correlationId: CorrelationId,
    val state: State
) : SubmittedReq
