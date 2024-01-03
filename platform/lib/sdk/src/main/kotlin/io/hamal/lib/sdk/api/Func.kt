package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFuncService.FuncQuery
import io.hamal.lib.sdk.fold

data class ApiFuncCreateRequest(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeValue
) : FuncCreateRequest

data class ApiFuncCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val funcId: FuncId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiRequested

data class ApiFuncDeployRequest(
    override val version: CodeVersion?,
    override val message: DeployMessage? = null
) : FuncDeployRequest

data class ApiFuncDeployRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val funcId: FuncId
) : ApiRequested

data class ApiFuncUpdateRequest(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null,
) : FuncUpdateRequest


data class ApiFuncUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val funcId: FuncId
) : ApiRequested


data class ApiFuncInvokeRequest(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val invocation: Invocation,
) : FuncInvokeRequest

data class ApiFuncInvokeVersionRequest(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val version: CodeVersion?
) : FuncInvokeVersionRequest

data class ApiFuncList(
    val funcs: List<Func>
) {
    data class Func(
        val id: FuncId,
        val flow: Flow,
        val name: FuncName
    ) {
        data class Flow(
            val id: FlowId,
            val name: FlowName
        )
    }
}

data class ApiFuncDeploymentList(
    val deployments: List<Deployment>
) {
    data class Deployment(
        val version: CodeVersion,
        val message: DeployMessage,
        val deployedAt: DeployedAt
    )
}


data class ApiFunc(
    val id: FuncId,
    val flow: Flow,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code,
    val deployment: Deployment
) {
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )

    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )

    data class Deployment(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue,
        val message: DeployMessage
    )
}

interface ApiFuncService {
    fun create(flowId: FlowId, createFuncReq: ApiFuncCreateRequest): ApiFuncCreateRequested
    fun deploy(funcId: FuncId, req: ApiFuncDeployRequest): ApiFuncDeployRequested
    fun list(query: FuncQuery): List<ApiFuncList.Func>
    fun listDeployments(funcId: FuncId): List<ApiFuncDeploymentList.Deployment>
    fun get(funcId: FuncId): ApiFunc
    fun update(funcId: FuncId, req: ApiFuncUpdateRequest): ApiFuncUpdateRequested

    fun invoke(funcId: FuncId, req: ApiFuncInvokeVersionRequest): ApiExecInvokeRequested

    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (flowIds.isNotEmpty()) req.parameter("flow_ids", flowIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiFuncServiceImpl(
    private val template: HttpTemplate
) : ApiFuncService {

    override fun create(flowId: FlowId, createFuncReq: ApiFuncCreateRequest) =
        template.post("/v1/flows/{flowId}/funcs")
            .path("flowId", flowId)
            .body(createFuncReq)
            .execute()
            .fold(ApiFuncCreateRequested::class)

    override fun deploy(funcId: FuncId, req: ApiFuncDeployRequest) =
        template.post("/v1/funcs/{funcId}/deploy")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiFuncDeployRequested::class)

    override fun list(query: FuncQuery): List<ApiFuncList.Func> =
        template.get("/v1/funcs")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiFuncList::class)
            .funcs

    override fun listDeployments(funcId: FuncId): List<ApiFuncDeploymentList.Deployment> =
        template.get("/v1/funcs/{funcId}/deployments")
            .path("funcId", funcId)
            .execute()
            .fold(ApiFuncDeploymentList::class)
            .deployments


    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(ApiFunc::class)

    override fun invoke(funcId: FuncId, req: ApiFuncInvokeVersionRequest) =
        template.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiExecInvokeRequested::class)

    override fun update(funcId: FuncId, req: ApiFuncUpdateRequest) =
        template.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiFuncUpdateRequested::class)
}