package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFuncService.FuncQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
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
    val namespaceId: NamespaceId
) : ApiSubmitted


@Serializable
data class ApiFuncUpdateReq(
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null,
) : UpdateFuncReq

/*@Serializable
data class ApiDeploymentSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId,
    val deployedVersion: CodeVersion
) : ApiSubmitted*/

@Serializable
data class ApiFuncUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val funcId: FuncId
) : ApiSubmitted

@Serializable
data class ApiFuncInvokeReq(
    override val correlationId: CorrelationId? = null,
    override val inputs: InvocationInputs? = null,
) : InvokeFuncReq

@Serializable
data class ApiFuncList(
    val funcs: List<Func>
) {
    @Serializable
    data class Func(
        val id: FuncId,
        val namespace: Namespace,
        val name: FuncName
    ) {
        @Serializable
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }
}


@Serializable
data class ApiFunc(
    val id: FuncId,
    val namespace: Namespace,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: Code
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )

    @Serializable
    data class Code(
        val id: CodeId,
        val version: CodeVersion,
        val value: CodeValue,
        val deployedVersion: CodeVersion
    )
}

interface ApiFuncService {
    fun create(namespaceId: NamespaceId, createFuncReq: ApiFuncCreateReq): ApiFuncCreateSubmitted
    fun deploy(funcId: FuncId, version: CodeVersion): ApiFuncUpdateSubmitted
    fun list(query: FuncQuery): List<ApiFuncList.Func>
    fun get(funcId: FuncId): ApiFunc
    fun invoke(funcId: FuncId, req: ApiFuncInvokeReq): ApiExecInvokeSubmitted

    data class FuncQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiFuncServiceImpl(
    private val template: HttpTemplateImpl
) : ApiFuncService {

    override fun create(namespaceId: NamespaceId, createFuncReq: ApiFuncCreateReq) =
        template.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
            .body(createFuncReq)
            .execute()
            .fold(ApiFuncCreateSubmitted::class)

    override fun deploy(funcId: FuncId, version: CodeVersion): ApiFuncUpdateSubmitted =
        template.post("/v1/funcs/{funcId}/deploy/{version}")
            .path("funcId", funcId)
            .path("version", version.value.toString())
            .execute()
            .fold(ApiFuncUpdateSubmitted::class)

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

    override fun invoke(funcId: FuncId, req: ApiFuncInvokeReq) =
        template.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiExecInvokeSubmitted::class)

}