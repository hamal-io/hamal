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
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.InvokeFuncVersionReq
import io.hamal.request.UpdateFuncReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiFuncCreateReq(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeValue
) : CreateFuncReq

@Serializable
data class ApiFuncCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val groupId: GroupId,
    val flowId: FlowId
) : ApiSubmitted

@Serializable
data class ApiFuncDeploySubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val version: CodeVersion
) : ApiSubmitted

@Serializable
data class ApiFuncDeployLatestSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val deployMessage: DeployMessage?
) : ApiSubmitted

@Serializable
data class ApiFuncUpdateReq(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null,
) : UpdateFuncReq


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
) : InvokeFuncReq

@Serializable
data class ApiInvokeFuncVersionReq(
    override val correlationId: CorrelationId?,
    override val inputs: InvocationInputs,
    override val version: CodeVersion?
) : InvokeFuncVersionReq

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
data class ApiFunc(
    val id: FuncId,
    val flow: Flow,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code
) {
    @Serializable
    data class Flow(
        val id: FlowId,
        val name: FlowName
    )

    @Serializable
    data class Code(
        val id: CodeId,
        val current: VersionedCodeValue,
        val deployed: VersionedCodeValue
    )

    @Serializable
    data class VersionedCodeValue(
        val version: CodeVersion,
        val value: CodeValue,
    )
}

interface ApiFuncService {
    fun create(flowId: FlowId, createFuncReq: ApiFuncCreateReq): ApiFuncCreateSubmitted
    fun deploy(funcId: FuncId, version: CodeVersion): ApiFuncDeploySubmitted
    fun deployLatest(funcId: FuncId): ApiFuncDeployLatestSubmitted
    fun list(query: FuncQuery): List<ApiFuncList.Func>
    fun get(funcId: FuncId): ApiFunc
    fun update(funcId: FuncId, req: ApiFuncUpdateReq): ApiFuncUpdateSubmitted

    fun invoke(funcId: FuncId, req: ApiInvokeFuncVersionReq): ApiExecInvokeSubmitted

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

    override fun deploy(funcId: FuncId, version: CodeVersion) =
        template.post("/v1/funcs/{funcId}/deploy/{version}")
            .path("funcId", funcId)
            .path("version", version.value.toString())
            .execute()
            .fold(ApiFuncDeploySubmitted::class)

    override fun deployLatest(funcId: FuncId): ApiFuncDeployLatestSubmitted =
        template.post("/v1/funcs/{funcId}/deploy/latest")
            .path("funcId", funcId)
            .execute()
            .fold(ApiFuncDeployLatestSubmitted::class)


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

    override fun invoke(funcId: FuncId, req: ApiInvokeFuncVersionReq) =
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