package io.hamal.lib.sdk.api

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold

data class ApiExecInvokeSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val execId: ExecId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted

data class ApiExecList(
    val execs: List<Exec>
) {
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
) {
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