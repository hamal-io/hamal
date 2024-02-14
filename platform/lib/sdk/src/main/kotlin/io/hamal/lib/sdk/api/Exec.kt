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
    fun list(workspaceId: WorkspaceId): List<ApiExecList.Exec>
    fun list(namespaceId: NamespaceId): List<ApiExecList.Exec>
    fun get(execId: ExecId): ApiExec
}

internal class ApiExecServiceImpl(
    private val template: HttpTemplate
) : ApiExecService {

    override fun list(workspaceId: WorkspaceId) =
        template.get("/v1/execs").parameter("workspace_ids", workspaceId).execute().fold(ApiExecList::class).execs

    override fun list(namespaceId: NamespaceId): List<ApiExecList.Exec> =
        template.get("/v1/namespaces/{namespaceId}/execs")
            .path("namespaceId", namespaceId)
            .execute()
            .fold(ApiExecList::class).execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}").path("execId", execId).execute().fold(ApiExec::class)
}