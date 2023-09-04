package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.CompleteExecReq
import io.hamal.lib.domain.req.FailExecReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("do not have separate dto")
data class ApiSimpleExecutionModel(
    val id: ExecId,
    val state: ExecStatus
)

@Serializable
@Deprecated("do not have separate dto")
data class ApiSimpleExecutionModels(
    val execs: List<ApiSimpleExecutionModel>
)

@Serializable
data class ApiExecList(
    val execs: List<SimpleExec>
) {
    @Serializable
    data class SimpleExec(
        val id: ExecId,
        val status: ExecStatus,
        val correlation: Correlation?,
        val func: Func?
    )

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

@Serializable
data class ApiExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: CodeType,
    val events: List<Event>
)

interface ExecService {
    fun poll(): ApiUnitOfWorkList

    //FIXME list of events to publish
    fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)

    // able to emit events on failure
    fun fail(execId: ExecId, error: ErrorType)
}

internal class DefaultExecService(
    private val template: HttpTemplate
) : ExecService {

    override fun poll(): ApiUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute(ApiUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(CompleteExecReq(stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(FailExecReq(error))
            .execute()
    }
}