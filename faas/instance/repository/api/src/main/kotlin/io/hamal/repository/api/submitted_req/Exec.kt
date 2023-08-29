package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.type.ErrorType
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedFailExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val cause: ErrorType
) : SubmittedReq

@Serializable
data class SubmittedCompleteExecReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: ExecId,
    val state: State,
    val events: List<Event>
) : SubmittedReq

