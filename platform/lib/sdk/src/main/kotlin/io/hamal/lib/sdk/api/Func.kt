package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.UpdateFuncReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiCreateFuncReq(
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeValue
) : CreateFuncReq

@Serializable
data class ApiUpdateFuncReq(
    override val namespaceId: NamespaceId? = null,
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeValue? = null
) : UpdateFuncReq

@Serializable
data class ApiInvokeFuncReq(
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
        val value: CodeValue
    )
}

interface ApiFuncService {
    fun create(namespaceId: NamespaceId, createFuncReq: ApiCreateFuncReq): ApiSubmittedReqWithId
    fun list(groupId: GroupId): List<ApiFuncList.Func>
    fun get(funcId: FuncId): ApiFunc
    fun invoke(funcId: FuncId, req: ApiInvokeFuncReq): ApiSubmittedReqWithId
}

internal class ApiFuncServiceImpl(
    private val template: HttpTemplateImpl
) : ApiFuncService {

    override fun create(namespaceId: NamespaceId, createFuncReq: ApiCreateFuncReq) =
        template.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
            .body(createFuncReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

    override fun list(groupId: GroupId): List<ApiFuncList.Func> =
        template.get("/v1/funcs")
            .parameter("group_ids", groupId)
            .execute()
            .fold(ApiFuncList::class)
            .funcs

    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(ApiFunc::class)

    override fun invoke(funcId: FuncId, req: ApiInvokeFuncReq) =
        template.post("/v1/funcs/{funcId}/invoke")
            .path("funcId", funcId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

}