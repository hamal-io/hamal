package io.hamal.lib.sdk.admin

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
import kotlinx.serialization.Serializable

@Serializable
data class AdminFailExecReq(
    val cause: ErrorType
)

@Serializable
data class AdminCompleteExecReq(
    val state: State,
    val events: List<EventToSubmit>
)

@Serializable
data class AdminExecList(
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
data class AdminExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val code: CodeType,
    val events: List<Event>
)

interface AdminExecService {
    fun poll(): AdminUnitOfWorkList
    fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>)
    fun fail(execId: ExecId, error: ErrorType)

    fun list(groupId: GroupId): List<AdminExecList.Exec>
    fun get(execId: ExecId): AdminExec
}

internal class DefaultAdminExecService(
    private val template: HttpTemplate
) : AdminExecService {

    override fun poll(): AdminUnitOfWorkList {
        return template.post("/v1/dequeue")
            .execute()
            .fold(AdminUnitOfWorkList::class)
    }

    override fun complete(execId: ExecId, stateAfterCompletion: State, eventToSubmit: List<EventToSubmit>) {
        template.post("/v1/execs/{execId}/complete")
            .path("execId", execId)
            .body(AdminCompleteExecReq(stateAfterCompletion, eventToSubmit))
            .execute()
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        template.post("/v1/execs/{execId}/fail")
            .path("execId", execId)
            .body(AdminFailExecReq(error))
            .execute()
    }

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/execs")
            .path("groupId", groupId)
            .execute()
            .fold(AdminExecList::class)
            .execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}")
            .path("execId", execId)
            .execute()
            .fold(AdminExec::class)
}