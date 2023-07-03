package io.hamal.lib.domain.req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.script.api.value.ErrorValue
import kotlinx.serialization.Serializable

@Serializable
data class FailExecReq(
    val reason: ErrorValue
)

@Serializable
data class SubmittedFailExecReq(
    override val id: ReqId,
    override var status: ReqStatus,
    val execId: ExecId,
    val reason: ErrorValue
) : SubmittedReq


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
) : SubmittedReq
