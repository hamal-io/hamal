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
import kotlinx.serialization.Serializable

@Serializable
data class ApiFuncCreateReq(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeValue
) : FuncCreateReq

@Serializable
data class ApiFuncCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted

@Serializable
data class ApiFuncDeployReq(
    override val version: CodeVersion?,
    override val message: DeployMessage? = null
) : FuncDeployReq

@Serializable
data class ApiFuncDeploySubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId
) : ApiSubmitted

@Serializable
data class ApiFuncUpdateReq(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null,
) : FuncUpdateReq


@Serializable
data class ApiFuncUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId
) : ApiSubmitted


@Serializable
data class ApiFuncInvokeReq(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val invocation: Invocation,
) : FuncInvokeReq

@Serializable
data class ApiFuncInvokeVersionReq(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val version: CodeVersion?
) : FuncInvokeVersionReq

@Serializable
data class ApiFuncList(
    val funcs: List<Func>
) {
    @Serializable
    data class Func(
        val id: FuncId,
        val flow: Flow,
        val name: FuncName
    ) {
        @Serializable
        data class Flow(
            val id: FlowId,
            val name: FlowName
        )
    }
}

@Serializable
data class ApiFuncDeploymentList(
    val deployments: List<Deployment>
) {
    @Serializable
    data class Deployment(
        val version: CodeVersion,
        val message: DeployMessage,
        val deployedAt: DeployedAt
    )
}


@Serializable
data class ApiFunc(
    val id: FuncId,
    val flow: Flow,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code,
    val deployment: Deployment
) {
    @Serializable
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )

    @Serializable
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue
    )

    @Serializable
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