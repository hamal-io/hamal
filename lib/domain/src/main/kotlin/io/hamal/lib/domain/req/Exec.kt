package io.hamal.lib.domain.req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.Serializable

@Serializable
data class CompleteExecReq(
    val state: State,
    val events: List<Event>
)

@Serializable
data class SubmittedCompleteExecReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val state: State,
    val events: List<Event>
) : Req
