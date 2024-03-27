package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold

data class ApiExecInvokeRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: ExecId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId
) : ApiRequested()

data class ApiExecList(
    val execs: List<Exec>
) : ApiObject() {
    data class Exec(
        val id: ExecId,
        val status: ExecStatus,
        val correlation: CorrelationId?,
        val namespace: Namespace,
        val invocation: Invocation,
        val func: Func?,
    )

    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )

    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

data class ApiExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: CorrelationId?,
    val inputs: ExecInputs,
    val invocation: Invocation,
    val result: ExecResult?,
    val state: ExecState?,
    val func: Func?
) : ApiObject() {

    data class Func(
        val id: FuncId,
        val name: FuncName
    )

    data class Code(
        val id: CodeId?,
        val version: CodeVersion?,
        val value: CodeValue?
    )
}

interface ApiExecService {
    fun list(query: ExecQuery): List<ApiExecList.Exec>
    fun get(execId: ExecId): ApiExec

    data class ExecQuery(
        val workspaceIds: List<WorkspaceId>,
        val namespaceIds: List<NamespaceId>
    )
}

internal class ApiExecServiceImpl(
    private val template: HttpTemplate
) : ApiExecService {

    override fun list(query: ApiExecService.ExecQuery) =
        template.get("/v1/execs")
            .parameter("workspace_ids", query.workspaceIds)
            .parameter("namespace_ids", query.namespaceIds)
            .execute()
            .fold(ApiExecList::class).execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}")
            .path("execId", execId)
            .execute()
            .fold(ApiExec::class)
}