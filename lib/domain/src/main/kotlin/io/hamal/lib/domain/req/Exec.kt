package io.hamal.lib.domain.req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.value.ErrorValue
import kotlinx.serialization.Serializable

@Serializable
data class FailExecReq(
    val cause: ErrorValue
)

@Serializable
data class SubmittedFailExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val cause: ErrorValue
) : SubmittedReq


@Serializable
data class CompleteExecReq(
    val state: State,
    val events: List<Event>
)

@Serializable
data class SubmittedCompleteExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val state: State,
    val events: List<Event>
) : SubmittedReq
