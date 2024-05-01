package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FuncCreateRequest
import io.hamal.lib.domain.request.FuncDeployRequest
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.request.FuncUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFuncService.FuncQuery
import io.hamal.lib.sdk.fold

data class ApiFuncCreateRequest(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: ValueCode,
    override val codeType: CodeType
) : FuncCreateRequest

data class ApiFuncCreateRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: FuncId,
    val workspaceId: WorkspaceId,
    val namespaceId: NamespaceId
) : ApiRequested()

data class ApiFuncDeployRequest(
    override val version: CodeVersion?,
    override val message: DeployMessage? = null
) : FuncDeployRequest

data class ApiFuncDeployRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: FuncId
) : ApiRequested()

data class ApiFuncUpdateRequest(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: ValueCode? = null,
) : FuncUpdateRequest


data class ApiFuncUpdateRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: FuncId
) : ApiRequested()


data class ApiFuncInvokeRequest(
    override val correlationId: CorrelationId? = null,
    override val inputs: InvocationInputs? = null,
    override val version: CodeVersion? = null
) : FuncInvokeRequest

data class ApiFuncList(
    val funcs: List<Func>
) : ApiObject() {
    data class Func(
        val id: FuncId,
        val namespace: Namespace,
        val name: FuncName
    ) {
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }
}

data class ApiFuncDeploymentList(
    val deployments: List<Deployment>
) : ApiObject() {
    data class Deployment(
        val version: CodeVersion,
        val message: DeployMessage,
        val deployedAt: DeployedAt
    )
}


data class ApiFunc(
    val id: FuncId,
    val namespace: Namespace,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code,
    val deployment: Deployment
) : ApiObject() {
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )

    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: ValueCode
    )

    data class Deployment(
        val id: CodeId,
        val version: CodeVersion,
        val value: ValueCode,
        val message: DeployMessage
    )
}

interface ApiFuncService {
    fun create(namespaceId: NamespaceId, createFuncReq: ApiFuncCreateRequest): ApiFuncCreateRequested
    fun deploy(funcId: FuncId, req: ApiFuncDeployRequest): ApiFuncDeployRequested
    fun list(query: FuncQuery): List<ApiFuncList.Func>
    fun listDeployments(funcId: FuncId): List<ApiFuncDeploymentList.Deployment>
    fun get(funcId: FuncId): ApiFunc
    fun update(funcId: FuncId, req: ApiFuncUpdateRequest): ApiFuncUpdateRequested

    fun invoke(funcId: FuncId, req: ApiFuncInvokeRequest): ApiExecInvokeRequested

    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (workspaceIds.isNotEmpty()) req.parameter("workspace_ids", workspaceIds)
        }
    }
}

internal class ApiFuncServiceImpl(
    private val template: HttpTemplate
) : ApiFuncService {

    override fun create(namespaceId: NamespaceId, createFuncReq: ApiFuncCreateRequest) =
        template.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
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

    override fun invoke(funcId: FuncId, req: ApiFuncInvokeRequest) =
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