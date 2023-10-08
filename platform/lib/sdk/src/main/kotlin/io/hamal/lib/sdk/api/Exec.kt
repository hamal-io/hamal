package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.fold
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiFailExecReq(
    override val cause: ErrorType
) : FailExecReq

@Serializable
data class ApiCompleteExecReq(
    override val state: State,
    override val events: List<EventToSubmit>
) : CompleteExecReq

@Serializable
data class ApiExecList(
    val execs: List<Exec>
) {
    @Serializable
    data class Exec(
        val id: ExecId,
        val status: ExecStatus,
        val correlation: Correlation?,
    )
}

@Serializable
data class ApiExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: CodeValue?,
    val codeId: CodeId?,
    val codeVersion: CodeVersion?,
    val events: List<Event>
)

interface ApiExecService {
    fun poll(): ApiUnitOfWorkList
    fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)
    fun fail(execId: ExecId, error: ErrorType)

    fun list(groupId: GroupId): List<ApiExecList.Exec>
    fun get(execId: ExecId): ApiExec
}

internal class ApiExecServiceImpl(
    private val template: HttpTemplate
) : ApiExecService {

    override fun poll(): ApiUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(ApiUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(ApiCompleteExecReq(stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(ApiFailExecReq(error))
            .execute()
    }

    override fun list(groupId: GroupId) =
        template.get("/v1/execs")
            .parameter("group_ids", groupId)
            .execute()
            .fold(ApiExecList::class)
            .execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}")
            .path("execId", execId)
            .execute()
            .fold(ApiExec::class)
}