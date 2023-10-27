package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import kotlinx.serialization.Serializable

@Serializable
data class BridgeFailExecReq(
    override val result: ExecResult
) : FailExecReq

@Serializable
data class BridgeCompleteExecReq(
    override val result: ExecResult,
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
        val namespaceId: NamespaceId,
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
    fun complete(execId: ExecId, result: ExecResult, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)
    fun fail(execId: ExecId, result: ExecResult)
}

internal class BridgeExecServiceImpl(
    private val template: HttpTemplateImpl
) : BridgeExecService {

    override fun poll(): BridgeUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(BridgeUnitOfWorkList::class)
    }

    override fun complete(
        execId: ExecId,
        result: ExecResult,
        stateAfterCompletion: State,
        eventToSubmit: List<EventToSubmit>
    ) {
        template.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(BridgeCompleteExecReq(result, stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(
        execId: ExecId,
        result: ExecResult
    ) {
        template.post("/b1/execs/{execId}/fail")
            .path("execId", execId)
            .body(BridgeFailExecReq(result))
            .execute()
    }
}