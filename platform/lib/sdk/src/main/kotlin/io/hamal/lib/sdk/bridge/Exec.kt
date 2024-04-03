package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class BridgeExecCompleteRequest(
    override val result: ExecResult,
    override val state: ExecState,
    override val events: List<EventToSubmit>
) : ExecCompleteRequest

data class BridgeExecCompleteRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val execId: ExecId,
) : BridgeRequested()

data class BridgeExecFailRequest(
    override val result: ExecResult
) : ExecFailRequest


data class BridgeExecFailRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val execId: ExecId,
) : BridgeRequested()

data class BridgeUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    data class UnitOfWork(
        val id: ExecId,
        val execToken: ExecToken,
        val namespaceId: NamespaceId,
        val workspaceId: WorkspaceId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeValue,
        val codeType: CodeType,
        val correlation: Correlation? = null
    )
}


interface BridgeExecService {
    fun poll(): BridgeUnitOfWorkList
    fun complete(
        execId: ExecId,
        result: ExecResult,
        state: ExecState,
        eventToSubmit: List<EventToSubmit>
    ): BridgeExecCompleteRequested

    fun fail(
        execId: ExecId,
        result: ExecResult
    ): BridgeExecFailRequested
}

internal class BridgeExecServiceImpl(
    private val template: HttpTemplate
) : BridgeExecService {

    override fun poll(): BridgeUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(BridgeUnitOfWorkList::class)
    }

    override fun complete(
        execId: ExecId,
        result: ExecResult,
        state: ExecState,
        eventToSubmit: List<EventToSubmit>
    ): BridgeExecCompleteRequested {
        return template.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(BridgeExecCompleteRequest(result, state, eventToSubmit))
            .execute(BridgeExecCompleteRequested::class)
    }

    override fun fail(
        execId: ExecId,
        result: ExecResult
    ): BridgeExecFailRequested {
        return template.post("/b1/execs/{execId}/fail")
            .path("execId", execId)
            .body(BridgeExecFailRequest(result))
            .execute(BridgeExecFailRequested::class)
    }
}