package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.fold
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
import kotlinx.serialization.Serializable

@Serializable
data class HubFailExecReq(
    override val cause: ErrorType
) : FailExecReq

@Serializable
data class HubCompleteExecReq(
    override val state: State,
    override val events: List<EventToSubmit>
) : CompleteExecReq

@Serializable
data class HubExecList(
    val execs: List<Exec>
) {
    @Serializable
    data class Exec(
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
data class HubExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: CodeType,
    val events: List<Event>
)

interface HubExecService {
    fun poll(): HubUnitOfWorkList
    fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)
    fun fail(execId: ExecId, error: ErrorType)

    fun list(groupId: GroupId): List<HubExecList.Exec>
    fun get(execId: ExecId): HubExec
}

internal class DefaultHubExecService(
    private val template: HttpTemplate
) : HubExecService {

    override fun poll(): HubUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(HubUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(HubCompleteExecReq(stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(HubFailExecReq(error))
            .execute()
    }

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/execs")
            .path("groupId", groupId)
            .execute()
            .fold(HubExecList::class)
            .execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}")
            .path("execId", execId)
            .execute()
            .fold(HubExec::class)
}