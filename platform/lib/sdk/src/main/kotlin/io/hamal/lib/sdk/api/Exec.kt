package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold

data class ApiExecInvokeRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val execId: ExecId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId
) : ApiRequested()

data class ApiExecList(
    val execs: List<Exec>
) : ApiObject() {
    data class Exec(
        val id: ExecId,
        val status: ExecStatus,
        val correlation: Correlation?,
    )
}

data class ApiExec(
    val id: ExecId,
    val status: ExecStatus,
    val correlation: Correlation?,
    val inputs: ExecInputs,
    val invocation: Invocation,
    val result: ExecResult?,
    val state: ExecState?
) : ApiObject() {
    data class Code(
        val id: CodeId?,
        val version: CodeVersion?,
        val value: CodeValue?
    )
}

interface ApiExecService {
    fun list(query: Query): List<ApiExecList.Exec>
    fun get(execId: ExecId): ApiExec

    data class Query(
        val workspaceIds: List<WorkspaceId>,
        val namespaceIds: List<NamespaceId>
    )
}

internal class ApiExecServiceImpl(
    private val template: HttpTemplate
) : ApiExecService {

    override fun list(query: ApiExecService.Query) =
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