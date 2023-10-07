package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.fold
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import kotlinx.serialization.Serializable

@Serializable
data class BridgeFailExecReq(
    override val cause: ErrorType
) : FailExecReq

@Serializable
data class BridgeCompleteExecReq(
    override val state: State,
    override val events: List<EventToSubmit>
) : CompleteExecReq

@Serializable
data class BridgeUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val groupId: GroupId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeValue,
        val correlation: Correlation? = null,
        val events: List<Event>
    )
}


interface BridgeExecService {
    fun poll(): BridgeUnitOfWorkList
    fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)
    fun fail(execId: ExecId, error: ErrorType)
}

internal class BridgeExecServiceImpl(
    private val template: HttpTemplate
) : BridgeExecService {

    override fun poll(): BridgeUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(BridgeUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(BridgeCompleteExecReq(stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(BridgeFailExecReq(error))
            .execute()
    }
}