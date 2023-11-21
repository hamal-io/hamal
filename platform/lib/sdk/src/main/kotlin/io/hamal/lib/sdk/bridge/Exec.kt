package io.hamal.lib.sdk.bridge

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import kotlinx.serialization.Serializable

@Serializable
data class BridgeExecCompleteReq(
    override val result: ExecResult,
    override val state: ExecState,
    override val events: List<EventToSubmit>
) : CompleteExecReq

@Serializable
data class BridgeExecCompleteSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val execId: ExecId,
) : BridgeSubmitted

@Serializable
data class BridgeExecFailReq(
    override val result: ExecResult
) : FailExecReq


@Serializable
data class BridgeExecFailSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val execId: ExecId,
) : BridgeSubmitted

@Serializable
data class BridgeUnitOfWorkList(
    val work: List<UnitOfWork>
) {
    @Serializable
    data class UnitOfWork(
        val id: ExecId,
        val flowId: FlowId,
        val groupId: GroupId,
        val inputs: ExecInputs,
        val state: State,
        val code: CodeValue,
        val correlation: Correlation? = null,
        val invocation: Invocation
    )
}


interface BridgeExecService {
    fun poll(): BridgeUnitOfWorkList
    fun complete(
        execId: ExecId,
        result: ExecResult,
        state: ExecState,
        eventToSubmit: List<EventToSubmit>
    ): BridgeExecCompleteSubmitted

    fun fail(execId: ExecId, result: ExecResult): BridgeExecFailSubmitted
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
    ): BridgeExecCompleteSubmitted {
        return template.post("/b1/execs/{execId}/complete")
            .path("execId", execId)
            .body(BridgeExecCompleteReq(result, state, eventToSubmit))
            .execute(BridgeExecCompleteSubmitted::class)
    }

    override fun fail(
        execId: ExecId,
        result: ExecResult
    ): BridgeExecFailSubmitted {
        return template.post("/b1/execs/{execId}/fail")
            .path("execId", execId)
            .body(BridgeExecFailReq(result))
            .execute(BridgeExecFailSubmitted::class)
    }
}