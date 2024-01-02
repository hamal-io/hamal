package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFuncService.FuncQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.*

data class ApiFuncCreateReq(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeValue
) : FuncCreateReq

data class ApiFuncCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted

data class ApiFuncDeployReq(
    override val version: CodeVersion?,
    override val message: DeployMessage? = null
) : FuncDeployReq

data class ApiFuncDeploySubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId
) : ApiSubmitted

data class ApiFuncUpdateReq(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null,
) : FuncUpdateReq


data class ApiFuncUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId
) : ApiSubmitted


data class ApiFuncInvokeReq(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val invocation: Invocation,
) : FuncInvokeReq

data class ApiFuncInvokeVersionReq(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val version: CodeVersion?
) : FuncInvokeVersionReq

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
    fun create(flowId: FlowId, createFuncReq: ApiFuncCreateReq): ApiFuncCreateSubmitted
    fun deploy(funcId: FuncId, req: ApiFuncDeployReq): ApiFuncDeploySubmitted
    fun list(query: FuncQuery): List<ApiFuncList.Func>
    fun listDeployments(funcId: FuncId): List<ApiFuncDeploymentList.Deployment>
    fun get(funcId: FuncId): ApiFunc
    fun update(funcId: FuncId, req: ApiFuncUpdateReq): ApiFuncUpdateSubmitted

    fun invoke(funcId: FuncId, req: ApiFuncInvokeVersionReq): ApiExecInvokeSubmitted

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

    override fun create(flowId: FlowId, createFuncReq: ApiFuncCreateReq) =
        template.post("/v1/flows/{flowId}/funcs")
            .path("flowId", flowId)
            .body(createFuncReq)
            .execute()
            .fold(ApiFuncCreateSubmitted::class)

    override fun deploy(funcId: FuncId, req: ApiFuncDeployReq) =
        template.post("/v1/funcs/{funcId}/deploy")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiFuncDeploySubmitted::class)

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

    override fun invoke(funcId: FuncId, req: ApiFuncInvokeVersionReq) =
        template.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiExecInvokeSubmitted::class)

    override fun update(funcId: FuncId, req: ApiFuncUpdateReq) =
        template.patch("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiFuncUpdateSubmitted::class)
}