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
    val groupId: GroupId,
    val flowId: FlowId
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
    fun list(groupId: GroupId): List<ApiExecList.Exec>
    fun list(flowId: FlowId): List<ApiExecList.Exec>
    fun get(execId: ExecId): ApiExec
}

internal class ApiExecServiceImpl(
    private val template: HttpTemplate
) : ApiExecService {

    override fun list(groupId: GroupId) =
        template.get("/v1/execs").parameter("group_ids", groupId).execute().fold(ApiExecList::class).execs

    override fun list(flowId: FlowId): List<ApiExecList.Exec> =
        template.get("/v1/flows/{flowId}/execs")
            .path("flowId", flowId)
            .execute()
            .fold(ApiExecList::class).execs

    override fun get(execId: ExecId) =
        template.get("/v1/execs/{execId}").path("execId", execId).execute().fold(ApiExec::class)
}